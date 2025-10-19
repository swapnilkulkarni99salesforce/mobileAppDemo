package com.example.perfectfit

import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.perfectfit.adapters.PortfolioAdapter
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentPortfolioBinding
import com.example.perfectfit.models.OrderImage
import com.example.perfectfit.utils.ImageHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragment displaying a gallery of completed work (portfolio images).
 * 
 * Features:
 * - Grid layout showing all portfolio images
 * - Full-screen image viewer on tap
 * - Share and delete actions on long-press
 * - Empty state when no images
 * - Real-time updates via LiveData
 * 
 * UI Components:
 * - Header with total count
 * - Grid RecyclerView (2 columns)
 * - Empty state illustration
 * - Full-screen dialog viewer
 * 
 * @see [OrderImage] for image entity
 * @see [PortfolioAdapter] for grid adapter
 * @see [ImageHelper] for image management
 */
class PortfolioFragment : Fragment() {

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var imageHelper: ImageHelper
    private lateinit var adapter: PortfolioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        imageHelper = ImageHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observePortfolioImages()
    }

    /**
     * Sets up the RecyclerView with grid layout and adapter.
     */
    private fun setupRecyclerView() {
        // Create adapter with click listeners
        adapter = PortfolioAdapter(
            onImageClick = { image, imageView ->
                showFullScreenImage(image)
            },
            onImageLongClick = { image ->
                showImageOptionsDialog(image)
            }
        )
        
        // Setup RecyclerView
        binding.portfolioRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = this@PortfolioFragment.adapter
            setHasFixedSize(true)
        }
    }

    /**
     * Observes portfolio images from database using LiveData.
     */
    private fun observePortfolioImages() {
        database.orderImageDao().getPortfolioImagesLiveData()
            .observe(viewLifecycleOwner) { images ->
                updateUI(images)
            }
    }

    /**
     * Updates UI based on portfolio images.
     * 
     * @param images List of portfolio images
     */
    private fun updateUI(images: List<OrderImage>) {
        if (images.isEmpty()) {
            // Show empty state
            binding.portfolioRecyclerView.isVisible = false
            binding.emptyState.isVisible = true
            binding.portfolioCount.text = "No completed work photos yet"
        } else {
            // Show images
            binding.portfolioRecyclerView.isVisible = true
            binding.emptyState.isVisible = false
            binding.portfolioCount.text = "${images.size} completed work ${if (images.size == 1) "photo" else "photos"}"
            
            // Update adapter
            adapter.updateImages(images)
        }
    }

    /**
     * Shows image in full-screen dialog.
     * 
     * @param image The image to display
     */
    private fun showFullScreenImage(image: OrderImage) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val imageView = ImageView(requireContext()).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            
            // Load image
            val fullPath = imageHelper.getFullPath(image.filePath)
            val bitmap = BitmapFactory.decodeFile(fullPath)
            if (bitmap != null) {
                setImageBitmap(bitmap)
            }
            
            // Close on click
            setOnClickListener {
                dialog.dismiss()
            }
            
            contentDescription = "Full screen image" + 
                if (image.caption.isNotEmpty()) ": ${image.caption}" else ""
        }
        
        dialog.setContentView(imageView)
        dialog.show()
    }

    /**
     * Shows options dialog for an image (share, delete).
     * 
     * @param image The image to act on
     */
    private fun showImageOptionsDialog(image: OrderImage) {
        val options = arrayOf("Share Image", "Delete Image", "Cancel")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Image Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> shareImage(image)
                    1 -> confirmDeleteImage(image)
                }
            }
            .show()
    }

    /**
     * Shares an image via intent.
     * 
     * @param image The image to share
     */
    private fun shareImage(image: OrderImage) {
        try {
            val uri = imageHelper.getImageUri(image.filePath)
            if (uri != null) {
                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "Portfolio Image")
                    
                    if (image.caption.isNotEmpty()) {
                        putExtra(android.content.Intent.EXTRA_TEXT, image.caption)
                    }
                    
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                startActivity(android.content.Intent.createChooser(shareIntent, "Share Portfolio Image"))
            } else {
                Toast.makeText(requireContext(), "Image file not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error sharing image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shows confirmation dialog before deleting an image.
     * 
     * @param image The image to delete
     */
    private fun confirmDeleteImage(image: OrderImage) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Image?")
            .setMessage("This will permanently delete this portfolio image. This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteImage(image)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Deletes an image from database and storage.
     * 
     * @param image The image to delete
     */
    private fun deleteImage(image: OrderImage) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Delete from database
                    database.orderImageDao().delete(image)
                    
                    // Delete file
                    imageHelper.deleteImage(image.filePath)
                }
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Image deleted", Toast.LENGTH_SHORT).show()
                    // LiveData will automatically update the UI
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error deleting image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): PortfolioFragment {
            return PortfolioFragment()
        }
    }
}

