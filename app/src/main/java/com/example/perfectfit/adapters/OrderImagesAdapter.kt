package com.example.perfectfit.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.R
import com.example.perfectfit.models.OrderImage
import com.example.perfectfit.utils.ImageHelper

/**
 * Adapter for displaying order images in a horizontal RecyclerView.
 * Shows thumbnail images with type badges and delete buttons.
 */
class OrderImagesAdapter(
    private val imageHelper: ImageHelper,
    private val onImageClick: (OrderImage) -> Unit,
    private val onDeleteClick: (OrderImage) -> Unit
) : RecyclerView.Adapter<OrderImagesAdapter.ImageViewHolder>() {

    private var images: List<OrderImage> = emptyList()

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailImage: ImageView = view.findViewById(R.id.thumbnail_image)
        val typeBadge: TextView = view.findViewById(R.id.image_type_badge)
        val deleteButton: ImageView = view.findViewById(R.id.delete_image_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_image_thumbnail, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        
        // Load thumbnail
        val fullPath = imageHelper.getFullPath(image.filePath)
        val bitmap = BitmapFactory.decodeFile(fullPath)
        if (bitmap != null) {
            holder.thumbnailImage.setImageBitmap(bitmap)
        } else {
            holder.thumbnailImage.setImageResource(R.drawable.ic_image)
        }
        
        // Set type badge
        holder.typeBadge.text = when (image.imageType) {
            OrderImage.TYPE_REFERENCE -> "REF"
            OrderImage.TYPE_COMPLETED -> "DONE"
            OrderImage.TYPE_PROGRESS -> "WIP"
            OrderImage.TYPE_DEFECT -> "ISSUE"
            else -> "IMG"
        }
        
        // Set click listeners
        holder.itemView.setOnClickListener {
            onImageClick(image)
        }
        
        holder.deleteButton.setOnClickListener {
            onDeleteClick(image)
        }
    }

    override fun getItemCount(): Int = images.size

    /**
     * Updates the adapter with new image list.
     */
    fun updateImages(newImages: List<OrderImage>) {
        images = newImages
        notifyDataSetChanged()
    }
}

