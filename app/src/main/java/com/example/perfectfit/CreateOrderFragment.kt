package com.example.perfectfit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentCreateOrderBinding
import com.example.perfectfit.models.Customer
import com.example.perfectfit.models.Order
import com.example.perfectfit.models.WorkloadConfig
import com.example.perfectfit.utils.WorkloadHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class CreateOrderFragment : Fragment() {

    private var _binding: FragmentCreateOrderBinding? = null
    private val binding get() = _binding!!
    private var customer: Customer? = null
    private lateinit var database: AppDatabase
    private var selectedDeliveryDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customer = Customer(
                id = it.getInt("id"),
                firstName = it.getString("firstName") ?: "",
                lastName = it.getString("lastName") ?: "",
                address = it.getString("address") ?: "",
                mobile = it.getString("mobile") ?: "",
                alternateMobile = it.getString("alternateMobile") ?: "",
                birthDate = it.getString("birthDate") ?: ""
            )
        }
        database = AppDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupListeners()
        calculateAndSetEstimatedDeliveryDate()
    }

    private fun setupViews() {
        // Pre-populate customer name
        customer?.let {
            binding.customerNameInput.setText(it.fullName)
        }

        // Pre-populate order date with today's date
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.orderDateInput.setText(dateFormat.format(today.time))

        // Setup order type dropdown
        val orderTypes = arrayOf("Blouse", "Kurti and Pant")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, orderTypes)
        binding.orderTypeDropdown.setAdapter(adapter)
    }

    private fun setupListeners() {
        // Estimated delivery date picker
        binding.estimatedDeliveryInput.setOnClickListener {
            showDatePicker()
        }

        binding.estimatedDeliveryLayout.setEndIconOnClickListener {
            showDatePicker()
        }

        // Save order button
        binding.saveOrderButton.setOnClickListener {
            saveOrder()
        }

        // Cancel button
        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showDatePicker() {
        val calendar = selectedDeliveryDate ?: Calendar.getInstance()
        
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDeliveryDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                updateDeliveryDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Set minimum date to today
        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }

    private fun updateDeliveryDate() {
        selectedDeliveryDate?.let { deliveryDate ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.estimatedDeliveryInput.setText(dateFormat.format(deliveryDate.time))
            
            // Calculate days difference
            val today = Calendar.getInstance()
            val diffInMillis = deliveryDate.timeInMillis - today.timeInMillis
            val daysUntilDelivery = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
            
            // Update delivery info text
            val deliveryDateStr = dateFormat.format(deliveryDate.time)
            binding.deliveryInfoText.text = "Your estimated delivery date is in $daysUntilDelivery days on $deliveryDateStr"
            binding.deliveryInfoText.visibility = View.VISIBLE
        }
    }

    private fun calculateAndSetEstimatedDeliveryDate() {
        lifecycleScope.launch {
            try {
                val config = withContext(Dispatchers.IO) {
                    database.workloadConfigDao().getConfig() ?: WorkloadConfig()
                }
                
                val pendingOrders = withContext(Dispatchers.IO) {
                    database.orderDao().getAllOrders().filter { 
                        it.status.equals("Pending", ignoreCase = true) || 
                        it.status.equals("In Progress", ignoreCase = true)
                    }
                }
                
                // Check workload status and show warning if overbooked
                val status = WorkloadHelper.calculateWorkloadStatus(pendingOrders, config)
                
                withContext(Dispatchers.Main) {
                    when (status.statusLevel) {
                        WorkloadHelper.StatusLevel.OVERBOOKED -> {
                            Toast.makeText(
                                requireContext(),
                                "⚠️ WARNING: You're currently overbooked at ${status.utilizationPercentage}% capacity!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        WorkloadHelper.StatusLevel.BUSY -> {
                            Toast.makeText(
                                requireContext(),
                                "⚠️ You're running at high capacity (${status.utilizationPercentage}%)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            // Show positive message
                            Toast.makeText(
                                requireContext(),
                                "✅ You have good capacity available",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                
                // ✨ IMPROVED: Use realistic estimates instead of optimistic
                val estimates = WorkloadHelper.calculateDeliveryEstimates(pendingOrders.size, config)
                selectedDeliveryDate = estimates.realisticDate
                
                // ✨ QUICK WIN 1: Get confidence level
                val confidenceLevel = WorkloadHelper.getConfidenceLevel(pendingOrders.size)
                val confidenceEmoji = WorkloadHelper.getConfidenceEmoji(confidenceLevel)
                val confidenceText = WorkloadHelper.getConfidenceText(confidenceLevel)
                
                withContext(Dispatchers.Main) {
                    updateDeliveryDate()
                    
                    // ✨ NEW: Display confidence in UI card!
                    binding.confidenceIndicatorCard.visibility = View.VISIBLE
                    binding.confidenceEmoji.text = confidenceEmoji
                    binding.confidenceLevelText.text = confidenceText
                    binding.confidenceDetailText.text = if (estimates.daysDifference > 0) {
                        "Buffer: +${estimates.daysDifference} days | ${pendingOrders.size} pending orders"
                    } else {
                        "${pendingOrders.size} pending orders"
                    }
                    
                    // Show comparison with visual confidence indicator
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val optimisticStr = dateFormat.format(estimates.optimisticDate.time)
                    val realisticStr = dateFormat.format(estimates.realisticDate.time)
                    
                    val comparisonMessage = if (estimates.daysDifference > 0) {
                        """
                        📅 Delivery Date Set
                        
                        Using REALISTIC estimate: $realisticStr ✅
                        (Optimistic would be: $optimisticStr)
                        
                        Buffer: +${estimates.daysDifference} days
                        $confidenceEmoji Confidence: $confidenceText
                        
                        💡 Realistic dates lead to happier customers!
                        """.trimIndent()
                    } else {
                        "📅 Delivery date: $realisticStr\n$confidenceEmoji Confidence: $confidenceText"
                    }
                    
                    Toast.makeText(
                        requireContext(),
                        comparisonMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                // If calculation fails, don't populate the date
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Could not auto-calculate delivery date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun calculateDeliveryDate(pendingOrdersCount: Int, config: WorkloadConfig): Calendar {
        // Calculate total hours needed for all pending orders + this new order
        val totalOrdersToComplete = pendingOrdersCount + 1
        val totalHoursNeeded = totalOrdersToComplete * config.timePerOrderHours
        
        val currentDate = Calendar.getInstance()
        var hoursRemaining = totalHoursNeeded
        var daysChecked = 0
        val maxDaysToCheck = 365 // Safety limit
        
        // Start from today and add working hours day by day
        while (hoursRemaining > 0 && daysChecked < maxDaysToCheck) {
            val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
            val workingHoursToday = config.getHoursForDay(dayOfWeek)
            
            // Deduct the working hours for this day
            hoursRemaining -= workingHoursToday
            
            // If we still have hours remaining, move to the next day
            if (hoursRemaining > 0) {
                currentDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            
            daysChecked++
        }
        
        // If we've exhausted the working hours or reached the limit, return the calculated date
        return currentDate
    }

    private fun saveOrder() {
        val orderType = binding.orderTypeDropdown.text.toString()
        val deliveryDate = binding.estimatedDeliveryInput.text.toString()
        val instructions = binding.instructionsInput.text.toString()
        val amountStr = binding.amountInput.text.toString()

        // Validation
        if (orderType.isEmpty()) {
            binding.orderTypeLayout.error = "Please select an order type"
            return
        }

        if (deliveryDate.isEmpty()) {
            binding.estimatedDeliveryLayout.error = "Please select an estimated delivery date"
            return
        }

        if (amountStr.isEmpty()) {
            binding.amountLayout.error = "Please enter the order amount"
            return
        }

        val amount = try {
            amountStr.toDouble()
        } catch (e: NumberFormatException) {
            binding.amountLayout.error = "Please enter a valid amount"
            return
        }

        if (amount <= 0) {
            binding.amountLayout.error = "Amount must be greater than 0"
            return
        }

        customer?.let { cust ->
            val order = Order(
                customerId = cust.id,
                customerName = cust.fullName,
                orderDate = binding.orderDateInput.text.toString(),
                orderType = orderType,
                estimatedDeliveryDate = deliveryDate,
                instructions = instructions,
                amount = amount,
                status = "Pending"
            )

            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        database.orderDao().insert(order)
                    }
                    
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Order created successfully!", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error creating order: ${e.message}", Toast.LENGTH_LONG).show()
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
        fun newInstance(customer: Customer): CreateOrderFragment {
            val fragment = CreateOrderFragment()
            val bundle = Bundle().apply {
                putInt("id", customer.id)
                putString("firstName", customer.firstName)
                putString("lastName", customer.lastName)
                putString("address", customer.address)
                putString("mobile", customer.mobile)
                putString("alternateMobile", customer.alternateMobile)
                putString("birthDate", customer.birthDate)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

