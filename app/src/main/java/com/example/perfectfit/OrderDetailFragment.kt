package com.example.perfectfit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentOrderDetailBinding
import com.example.perfectfit.models.Order
import com.example.perfectfit.utils.WhatsAppHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private var order: Order? = null
    private lateinit var database: AppDatabase
    private var customerPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        
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

