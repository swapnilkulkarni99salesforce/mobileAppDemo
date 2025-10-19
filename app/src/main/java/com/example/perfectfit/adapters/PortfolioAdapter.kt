package com.example.perfectfit.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.R
import com.example.perfectfit.models.OrderImage
import com.example.perfectfit.utils.ImageHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying portfolio images in a grid layout.
 * 
 * Features:
 * - Grid display of completed work photos
 * - Click listeners for full-screen view
 * - Long-press for actions (share, delete)
 * - Efficient updates with DiffUtil
 * - Memory-efficient image loading
 * 
 * @property onImageClick Callback when image is tapped (for full-screen view)
 * @property onImageLongClick Callback when image is long-pressed (for actions menu)
 */
class PortfolioAdapter(
    private val onImageClick: (OrderImage, ImageView) -> Unit = { _, _ -> },
    private val onImageLongClick: (OrderImage) -> Unit = {}
) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    private var images = listOf<OrderImage>()
    private lateinit var imageHelper: ImageHelper

    /**
     * ViewHolder for portfolio image items.
     * 
     * @property itemView The item view layout
     */
    class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.portfolio_image)
        val captionText: TextView = itemView.findViewById(R.id.portfolio_caption)
        val dateText: TextView = itemView.findViewById(R.id.portfolio_date)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        imageHelper = ImageHelper(recyclerView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_portfolio_image, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val image = images[position]
        
        // Load image from storage
        val fullPath = imageHelper.getFullPath(image.filePath)
        try {
            val bitmap = BitmapFactory.decodeFile(fullPath)
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap)
            } else {
                holder.imageView.setImageResource(R.drawable.ic_image_placeholder)
            }
        } catch (e: Exception) {
            holder.imageView.setImageResource(R.drawable.ic_image_placeholder)
        }
        
        // Set caption
        if (image.caption.isNotEmpty()) {
            holder.captionText.visibility = View.VISIBLE
            holder.captionText.text = image.caption
        } else {
            holder.captionText.visibility = View.GONE
        }
        
        // Set upload date
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.dateText.text = dateFormat.format(Date(image.uploadedAt))
        
        // Set click listeners
        holder.itemView.setOnClickListener {
            onImageClick(image, holder.imageView)
        }
        
        holder.itemView.setOnLongClickListener {
            onImageLongClick(image)
            true
        }
        
        // Add content description for accessibility
        holder.imageView.contentDescription = "Portfolio image" + 
            if (image.caption.isNotEmpty()) ": ${image.caption}" else ""
    }

    override fun getItemCount(): Int = images.size

    /**
     * Updates the portfolio images list using DiffUtil for efficient updates.
     * 
     * @param newImages New list of portfolio images
     */
    fun updateImages(newImages: List<OrderImage>) {
        val diffCallback = PortfolioDiffCallback(images, newImages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        
        images = newImages
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * DiffUtil callback for calculating differences between old and new image lists.
     * 
     * This enables efficient RecyclerView updates with automatic animations
     * for insertions, deletions, and moves.
     * 
     * @property oldList Previous list of images
     * @property newList New list of images
     */
    private class PortfolioDiffCallback(
        private val oldList: List<OrderImage>,
        private val newList: List<OrderImage>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldList[oldItemPosition]
            val new = newList[newItemPosition]
            
            return old.filePath == new.filePath &&
                   old.caption == new.caption &&
                   old.uploadedAt == new.uploadedAt &&
                   old.displayOrder == new.displayOrder
        }
    }
}

