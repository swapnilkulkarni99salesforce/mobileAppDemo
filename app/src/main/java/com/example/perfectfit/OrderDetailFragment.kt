package com.example.perfectfit

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perfectfit.adapters.OrderImagesAdapter
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentOrderDetailBinding
import com.example.perfectfit.models.Order
import com.example.perfectfit.models.OrderImage
import com.example.perfectfit.models.ProductionStage
import com.example.perfectfit.models.OrderStageHistory
import com.example.perfectfit.utils.ImageHelper
import com.example.perfectfit.utils.WhatsAppHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private var order: Order? = null
    private lateinit var database: AppDatabase
    private var customerPhone: String = ""
    private var currentStage: ProductionStage? = null
    
    // Image handling
    private lateinit var imageHelper: ImageHelper
    private lateinit var imagesAdapter: OrderImagesAdapter
    private var pendingImageType: String? = null
    private var tempCameraImageUri: Uri? = null
    
    // Gallery picker launcher
    private val galleryPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleImageSelected(uri)
            }
        }
    }
    
    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            tempCameraImageUri?.let { uri ->
                handleImageSelected(uri)
            }
        }
    }
    
    // Camera permission launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        imageHelper = ImageHelper(requireContext())
        
        arguments?.let {
            val orderId = it.getInt("orderId")
            loadOrder(orderId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupStatusDropdown()
        setupListeners()
        setupProductionTracking()
        setupOrderImages()
    }

    private fun loadOrder(orderId: Int) {
        lifecycleScope.launch {
            try {
                val loadedOrder = withContext(Dispatchers.IO) {
                    database.orderDao().getOrderById(orderId)
                }
                
                withContext(Dispatchers.Main) {
                    loadedOrder?.let {
                        order = it
                        displayOrderDetails(it)
                        loadProductionStage(it.id)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error loading order: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun displayOrderDetails(order: Order) {
        binding.orderIdText.text = order.orderId
        binding.detailCustomerName.text = order.customerName
        binding.detailOrderType.text = order.orderType
        binding.detailOrderDate.text = order.orderDate
        binding.detailDeliveryDate.text = order.estimatedDeliveryDate
        binding.detailAmount.text = order.formattedAmount
        binding.statusDropdown.setText(order.status, false)
        
        if (order.instructions.isNotEmpty()) {
            binding.detailInstructions.text = order.instructions
        } else {
            binding.detailInstructions.text = "No special instructions"
        }
        
        // Display payment details
        displayPaymentDetails(order)
        
        // Load customer phone number
        loadCustomerPhone(order.customerId)
    }
    
    private fun displayPaymentDetails(order: Order) {
        // Update payment status badge
        binding.paymentStatusBadge.text = order.paymentStatus
        val statusBg = when (order.paymentStatus) {
            Order.PAYMENT_PAID -> R.drawable.status_completed_bg
            Order.PAYMENT_PARTIAL -> R.drawable.status_in_progress_bg
            else -> R.drawable.status_pending_bg
        }
        binding.paymentStatusBadge.setBackgroundResource(statusBg)
        
        // Update amounts
        binding.paymentTotalAmount.text = order.formattedAmount
        binding.paymentAdvance.text = "₹${String.format("%.2f", order.advancePayment)}"
        binding.paymentBalancePaid.text = "₹${String.format("%.2f", order.balancePayment)}"
        binding.paymentOutstanding.text = order.formattedOutstanding
        
        // Change outstanding color based on amount
        val outstandingColor = if (order.outstandingAmount > 0) {
            android.R.color.holo_red_dark
        } else {
            android.R.color.holo_green_dark
        }
        binding.paymentOutstanding.setTextColor(
            ContextCompat.getColor(requireContext(), outstandingColor)
        )
        
        // Enable/disable record payment button
        binding.recordPaymentButton.isEnabled = order.outstandingAmount > 0
        if (order.outstandingAmount <= 0) {
            binding.recordPaymentButton.text = "✅ Fully Paid"
        }
    }
    
    private fun loadCustomerPhone(customerId: Int) {
        lifecycleScope.launch {
            try {
                val customer = withContext(Dispatchers.IO) {
                    database.customerDao().getCustomerById(customerId)
                }
                customer?.let {
                    customerPhone = it.mobile
                }
            } catch (e: Exception) {
                // Handle silently
            }
        }
    }

    private fun setupStatusDropdown() {
        val statuses = arrayOf("Pending", "In Progress", "Completed", "Closed")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statuses)
        binding.statusDropdown.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.updateStatusButton.setOnClickListener {
            updateOrderStatus()
        }
        
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        // Payment tracking listeners
        binding.recordPaymentButton.setOnClickListener {
            showRecordPaymentDialog()
        }
        
        // WhatsApp listeners
        binding.whatsappOrderReadyButton.setOnClickListener {
            sendOrderReadyMessage()
        }
        
        binding.whatsappPaymentReminderButton.setOnClickListener {
            sendPaymentReminder()
        }
        
        binding.whatsappCustomMessageButton.setOnClickListener {
            showCustomMessageDialog()
        }
        
        // Production tracking listeners
        binding.productionNextStageButton.setOnClickListener {
            showStageTransitionDialog()
        }
        
        binding.productionViewHistoryButton.setOnClickListener {
            showProductionHistory()
        }
        
        // Order images listeners
        binding.addReferenceImageButton.setOnClickListener {
            pickImage(OrderImage.TYPE_REFERENCE)
        }
        
        binding.addCompletedImageButton.setOnClickListener {
            pickImage(OrderImage.TYPE_COMPLETED)
        }
    }
    
    // ===== Production Tracking Methods =====
    
    private fun setupProductionTracking() {
        // Initial setup - actual data loaded in loadProductionStage()
    }
    
    private fun loadProductionStage(orderId: Int) {
        lifecycleScope.launch {
            try {
                val stage = withContext(Dispatchers.IO) {
                    database.productionStageDao().getStageByOrderId(orderId)
                }
                
                if (stage == null) {
                    // Create initial production stage
                    withContext(Dispatchers.IO) {
                        val newStage = ProductionStage(
                            orderId = orderId,
                            currentStage = ProductionStage.STAGE_PENDING
                        )
                        database.productionStageDao().insert(newStage)
                        
                        // Create initial history entry
                        val history = OrderStageHistory.startStage(
                            orderId = orderId,
                            stageName = ProductionStage.STAGE_PENDING
                        )
                        database.orderStageHistoryDao().insert(history)
                    }
                    loadProductionStage(orderId) // Reload
                } else {
                    withContext(Dispatchers.Main) {
                        currentStage = stage
                        displayProductionStage(stage)
                        loadStageTimeline(orderId)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error loading production stage: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun displayProductionStage(stage: ProductionStage) {
        binding.productionCurrentStage.text = stage.getStageDisplayName()
        binding.productionProgressBar.progress = stage.getProgressPercentage()
        binding.productionProgressPercentage.text = "${stage.getProgressPercentage()}%"
        
        val timeInStage = stage.getTimeInCurrentStage()
        binding.productionTimeInStage.text = when {
            timeInStage < 1 -> "In stage for: ${(timeInStage * 60).toInt()} minutes"
            else -> "In stage for: ${timeInStage.toInt()} hours"
        }
        
        // Check if delayed
        val expected = ProductionStage.getExpectedDuration(stage.currentStage)
        if (expected > 0 && stage.isStageDelayed(expected)) {
            binding.productionTimeInStage.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            )
            binding.productionTimeInStage.text = "${binding.productionTimeInStage.text} ⚠️ DELAYED"
        } else {
            binding.productionTimeInStage.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.white)
            )
        }
        
        // Show assigned worker if available
        if (stage.assignedTo.isNotEmpty()) {
            binding.productionAssignedWorker.visibility = View.VISIBLE
            binding.productionAssignedWorker.text = "Assigned to: ${stage.assignedTo}"
        }
        
        // Disable next stage button if already delivered
        if (stage.currentStage == ProductionStage.STAGE_DELIVERED) {
            binding.productionNextStageButton.isEnabled = false
            binding.productionNextStageButton.text = "✅ Delivered"
        }
    }
    
    private fun loadStageTimeline(orderId: Int) {
        lifecycleScope.launch {
            try {
                val history = withContext(Dispatchers.IO) {
                    database.orderStageHistoryDao().getHistoryByOrderId(orderId)
                }
                
                withContext(Dispatchers.Main) {
                    displayTimeline(history)
                }
            } catch (e: Exception) {
                // Handle silently
            }
        }
    }
    
    private fun displayTimeline(history: List<OrderStageHistory>) {
        binding.productionTimelineContainer.removeAllViews()
        
        if (history.isEmpty()) {
            val textView = TextView(requireContext()).apply {
                text = "No timeline data yet"
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            binding.productionTimelineContainer.addView(textView)
            return
        }
        
        history.forEach { stage ->
            val timelineItem = LayoutInflater.from(requireContext())
                .inflate(android.R.layout.simple_list_item_2, binding.productionTimelineContainer, false)
            
            val title = timelineItem.findViewById<TextView>(android.R.id.text1)
            val subtitle = timelineItem.findViewById<TextView>(android.R.id.text2)
            
            val duration = stage.getDurationHours()
            val durationText = if (duration != null) {
                String.format("%.1f hours", duration)
            } else {
                "In progress"
            }
            
            title.text = "${ProductionStage.getStageDisplayName(stage.stageName)} - $durationText"
            title.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            
            val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            subtitle.text = "Started: ${dateFormat.format(Date(stage.stageStartedAt))}"
            subtitle.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            
            if (stage.isDelayed()) {
                title.text = "${title.text} ⚠️"
                title.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
            }
            
            binding.productionTimelineContainer.addView(timelineItem)
        }
    }
    
    private fun showStageTransitionDialog() {
        val stage = currentStage ?: return
        val currentIndex = ProductionStage.getAllStages().indexOf(stage.currentStage)
        
        if (currentIndex == -1 || currentIndex >= ProductionStage.getAllStages().size - 1) {
            Toast.makeText(requireContext(), "Order is already at final stage", Toast.LENGTH_SHORT).show()
            return
        }
        
        val nextStage = ProductionStage.getAllStages()[currentIndex + 1]
        val nextStageDisplay = ProductionStage.getStageDisplayName(nextStage)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Move to Next Stage")
            .setMessage("Move order to: $nextStageDisplay?")
            .setPositiveButton("Move") { _, _ ->
                moveToNextStage(nextStage)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun moveToNextStage(newStage: String) {
        val orderId = order?.id ?: return
        
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Complete current stage in history
                    val currentHistory = database.orderStageHistoryDao().getActiveStageHistory(orderId)
                    currentHistory?.let {
                        database.orderStageHistoryDao().update(it.complete())
                    }
                    
                    // Create new stage history entry
                    val newHistory = OrderStageHistory.startStage(orderId, newStage)
                    database.orderStageHistoryDao().insert(newHistory)
                    
                    // Update current stage
                    database.productionStageDao().updateStage(orderId, newStage, System.currentTimeMillis())
                }
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Stage updated successfully!", Toast.LENGTH_SHORT).show()
                    loadProductionStage(orderId) // Reload to show updated data
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error updating stage: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun showProductionHistory() {
        val orderId = order?.id ?: return
        
        lifecycleScope.launch {
            try {
                val history = withContext(Dispatchers.IO) {
                    database.orderStageHistoryDao().getHistoryByOrderId(orderId)
                }
                
                val totalDuration = withContext(Dispatchers.IO) {
                    database.orderStageHistoryDao().getTotalOrderDuration(orderId)
                }
                
                withContext(Dispatchers.Main) {
                    val items = history.map { stage ->
                        val duration = stage.getDurationHours()
                        val durationText = if (duration != null) {
                            String.format("%.1f hrs", duration)
                        } else {
                            "In progress"
                        }
                        val delayed = if (stage.isDelayed()) " ⚠️ DELAYED" else ""
                        "${stage.stageName}: $durationText$delayed"
                    }.toTypedArray()
                    
                    val totalHours = totalDuration?.let { it / (1000f * 60f * 60f) } ?: 0f
                    val message = "Total time: ${String.format("%.1f", totalHours)} hours\n\n${items.joinToString("\n")}"
                    
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Complete Production History")
                        .setMessage(message)
                        .setPositiveButton("Close", null)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error loading history: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun showRecordPaymentDialog() {
        val currentOrder = order ?: return
        
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            android.R.layout.simple_list_item_1, null
        )
        
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Record Payment")
        
        // Create custom layout
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        
        val amountInput = EditText(requireContext()).apply {
            hint = "Enter payment amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or 
                       android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        
        layout.addView(android.widget.TextView(requireContext()).apply {
            text = "Outstanding: ${currentOrder.formattedOutstanding}"
            textSize = 16f
            setPadding(0, 0, 0, 20)
        })
        layout.addView(amountInput)
        
        builder.setView(layout)
        builder.setPositiveButton("Record") { _, _ ->
            val amountStr = amountInput.text.toString()
            val amount = amountStr.toDoubleOrNull()
            
            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            
            if (amount > currentOrder.outstandingAmount) {
                Toast.makeText(requireContext(), "Amount exceeds outstanding balance", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            
            recordPayment(amount)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun recordPayment(amount: Double) {
        val currentOrder = order ?: return
        
        lifecycleScope.launch {
            try {
                val newBalancePayment = currentOrder.balancePayment + amount
                val newOutstanding = currentOrder.amount - (currentOrder.advancePayment + newBalancePayment)
                
                val newPaymentStatus = when {
                    newOutstanding <= 0 -> Order.PAYMENT_PAID
                    newBalancePayment > 0 || currentOrder.advancePayment > 0 -> Order.PAYMENT_PARTIAL
                    else -> Order.PAYMENT_UNPAID
                }
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val paymentDate = if (newPaymentStatus == Order.PAYMENT_PAID) {
                    dateFormat.format(Date())
                } else null
                
                val updatedOrder = currentOrder.copy(
                    balancePayment = newBalancePayment,
                    paymentStatus = newPaymentStatus,
                    paymentDate = paymentDate
                )
                
                withContext(Dispatchers.IO) {
                    database.orderDao().update(updatedOrder)
                }
                
                withContext(Dispatchers.Main) {
                    order = updatedOrder
                    displayPaymentDetails(updatedOrder)
                    Toast.makeText(requireContext(), "Payment recorded successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error recording payment: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun sendOrderReadyMessage() {
        val currentOrder = order ?: return
        
        if (customerPhone.isEmpty()) {
            Toast.makeText(requireContext(), "Customer phone number not available", Toast.LENGTH_SHORT).show()
            return
        }
        
        WhatsAppHelper.sendOrderReadyMessage(requireContext(), currentOrder, customerPhone)
    }
    
    private fun sendPaymentReminder() {
        val currentOrder = order ?: return
        
        if (customerPhone.isEmpty()) {
            Toast.makeText(requireContext(), "Customer phone number not available", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (currentOrder.outstandingAmount <= 0) {
            Toast.makeText(requireContext(), "No outstanding payment for this order", Toast.LENGTH_SHORT).show()
            return
        }
        
        WhatsAppHelper.sendPaymentReminder(requireContext(), currentOrder, customerPhone)
    }
    
    private fun showCustomMessageDialog() {
        val currentOrder = order ?: return
        
        if (customerPhone.isEmpty()) {
            Toast.makeText(requireContext(), "Customer phone number not available", Toast.LENGTH_SHORT).show()
            return
        }
        
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Send Custom Message")
        
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        
        val messageInput = EditText(requireContext()).apply {
            hint = "Enter your message"
            minLines = 3
            maxLines = 5
        }
        
        layout.addView(messageInput)
        builder.setView(layout)
        
        builder.setPositiveButton("Send via WhatsApp") { _, _ ->
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                WhatsAppHelper.sendCustomMessage(
                    requireContext(),
                    customerPhone,
                    currentOrder.customerName,
                    message
                )
            } else {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun updateOrderStatus() {
        val newStatus = binding.statusDropdown.text.toString()
        
        if (newStatus.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a status", Toast.LENGTH_SHORT).show()
            return
        }
        
        order?.let { currentOrder ->
            val updatedOrder = currentOrder.copy(status = newStatus)
            
            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        database.orderDao().update(updatedOrder)
                    }
                    
                    withContext(Dispatchers.Main) {
                        order = updatedOrder
                        Toast.makeText(requireContext(), "Order status updated to: $newStatus", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error updating status: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    // ===== Order Images Methods =====
    
    /**
     * Sets up the order images RecyclerView and adapter.
     */
    private fun setupOrderImages() {
        // Initialize adapter
        imagesAdapter = OrderImagesAdapter(
            imageHelper = imageHelper,
            onImageClick = { image -> showFullScreenImage(image) },
            onDeleteClick = { image -> confirmDeleteImage(image) }
        )
        
        // Setup RecyclerView
        binding.orderImagesRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imagesAdapter
        }
        
        // Load images for this order
        order?.let { loadOrderImages(it.id) }
    }
    
    /**
     * Loads all images for the order.
     */
    private fun loadOrderImages(orderId: Int) {
        lifecycleScope.launch {
            try {
                val images = withContext(Dispatchers.IO) {
                    database.orderImageDao().getImagesByOrderId(orderId)
                }
                
                withContext(Dispatchers.Main) {
                    updateImagesUI(images)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error loading images: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    /**
     * Updates the images UI.
     */
    private fun updateImagesUI(images: List<OrderImage>) {
        if (images.isEmpty()) {
            binding.orderImagesRecycler.isVisible = false
            binding.noImagesText.isVisible = true
            binding.imagesCountText.text = "0"
        } else {
            binding.orderImagesRecycler.isVisible = true
            binding.noImagesText.isVisible = false
            binding.imagesCountText.text = images.size.toString()
            imagesAdapter.updateImages(images)
        }
    }
    
    /**
     * Shows chooser dialog for Camera or Gallery.
     */
    private fun pickImage(imageType: String) {
        pendingImageType = imageType
        
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> openGallery()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }
    
    /**
     * Checks camera permission and opens camera if granted.
     */
    private fun checkCameraPermissionAndOpen() {
        when {
            requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) == 
                android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
    
    /**
     * Opens the camera to take a photo.
     */
    private fun openCamera() {
        try {
            // Create temporary file for camera image
            val orderId = order?.id ?: return
            val imageType = pendingImageType ?: return
            val timestamp = System.currentTimeMillis()
            val fileName = "temp_camera_${orderId}_${imageType}_${timestamp}.jpg"
            
            val tempFile = File(requireContext().cacheDir, fileName)
            tempCameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                tempFile
            )
            
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, tempCameraImageUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Opens gallery to pick an image.
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryPickerLauncher.launch(intent)
    }
    
    /**
     * Handles the selected image from picker.
     */
    private fun handleImageSelected(uri: android.net.Uri) {
        val orderId = order?.id ?: return
        val imageType = pendingImageType ?: return
        
        lifecycleScope.launch {
            try {
                // Save image to internal storage
                val filePath = withContext(Dispatchers.IO) {
                    imageHelper.saveImage(uri, orderId, imageType)
                }
                
                if (filePath != null) {
                    // Save to database
                    val orderImage = OrderImage(
                        orderId = orderId,
                        filePath = filePath,
                        imageType = imageType,
                        uploadedAt = System.currentTimeMillis()
                    )
                    
                    withContext(Dispatchers.IO) {
                        database.orderImageDao().insert(orderImage)
                    }
                    
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Image added successfully!", Toast.LENGTH_SHORT).show()
                        loadOrderImages(orderId) // Reload images
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        pendingImageType = null
    }
    
    /**
     * Shows full-screen image viewer.
     */
    private fun showFullScreenImage(image: OrderImage) {
        val dialog = android.app.Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
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
            
            contentDescription = "Full screen order image"
        }
        
        dialog.setContentView(imageView)
        dialog.show()
    }
    
    /**
     * Shows confirmation dialog before deleting an image.
     */
    private fun confirmDeleteImage(image: OrderImage) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Image?")
            .setMessage("Are you sure you want to delete this image? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteImage(image)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    /**
     * Deletes an image from database and storage.
     */
    private fun deleteImage(image: OrderImage) {
        val orderId = order?.id ?: return
        
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
                    loadOrderImages(orderId) // Reload images
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
        fun newInstance(orderId: Int): OrderDetailFragment {
            val fragment = OrderDetailFragment()
            val bundle = Bundle().apply {
                putInt("orderId", orderId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

