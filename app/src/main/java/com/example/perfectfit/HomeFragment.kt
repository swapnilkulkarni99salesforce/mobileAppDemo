package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentHomeBinding
import com.example.perfectfit.models.Order
import com.example.perfectfit.models.WorkloadConfig
import com.example.perfectfit.sync.SyncRepository
import com.example.perfectfit.utils.WorkloadHelper
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var syncRepository: SyncRepository
    private lateinit var database: AppDatabase
    
    private val motivationalQuotes = listOf(
        "Success is the sum of small efforts repeated day in and day out.",
        "The secret to getting ahead is getting started.",
        "Quality is not an act, it is a habit.",
        "Excellence is not a destination, it is a continuous journey.",
        "Your reputation is more important than your paycheck.",
        "Attention to detail is the secret to success.",
        "Every customer deserves your best work.",
        "Perfection is not attainable, but if we chase perfection we can catch excellence.",
        "The difference between ordinary and extraordinary is that little extra.",
        "Great things are done by a series of small things brought together.",
        "Success usually comes to those who are too busy to be looking for it.",
        "Don't watch the clock; do what it does. Keep going.",
        "The only way to do great work is to love what you do.",
        "Believe in yourself and all that you are capable of achieving.",
        "Small progress is still progress. Keep moving forward!",
        "Your dedication today builds your success tomorrow.",
        "Every stitch you make is a step towards excellence.",
        "Customer satisfaction starts with your commitment to quality.",
        "Dream big, work hard, stay focused, and surround yourself with good people.",
        "The harder you work for something, the greater you'll feel when you achieve it."
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        syncRepository = SyncRepository(requireContext())
        setupGreetingAndQuote()
        setupClickListeners()
        setupSyncUI()
        observeSyncStatus()
        loadDashboardStatistics()
        loadWorkloadStatus()
        loadDeliveryAlerts()
        loadFinancialDashboard()
    }
    
    private fun setupGreetingAndQuote() {
        // Set time-based greeting
        val greeting = getTimeBasedGreeting()
        binding.greetingText.text = greeting.first
        binding.userNameText.text = greeting.second
        
        // Set random motivational quote
        val randomQuote = motivationalQuotes.random()
        binding.motivationalQuoteText.text = randomQuote
    }
    
    private fun getTimeBasedGreeting(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        
        return when (hourOfDay) {
            in 0..4 -> Pair("Good Night", "Time to rest and recharge for tomorrow!")
            in 5..11 -> Pair("Good Morning", "Let's start the day with productivity!")
            in 12..16 -> Pair("Good Afternoon", "Keep up the great work!")
            in 17..20 -> Pair("Good Evening", "Finishing strong today!")
            else -> Pair("Good Night", "Great job today! Time to unwind.")
        }
    }

    private fun setupClickListeners() {
        binding.registerCustomerButton.setOnClickListener {
            navigateToRegisterCustomer()
        }
        
        binding.workloadConfigButton.setOnClickListener {
            navigateToWorkloadConfig()
        }
        
        binding.syncNowButton.setOnClickListener {
            performSync()
        }
        
        binding.autoSyncSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                syncRepository.enableBackgroundSync()
                Toast.makeText(requireContext(), "Auto-sync enabled", Toast.LENGTH_SHORT).show()
            } else {
                syncRepository.disableBackgroundSync()
                Toast.makeText(requireContext(), "Auto-sync disabled", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.viewAllOrdersButton.setOnClickListener {
            navigateToOrders()
        }
        
        binding.viewPendingPaymentsButton.setOnClickListener {
            navigateToPendingPayments()
        }
    }
    
    private fun navigateToOrders() {
        // Switch to orders tab (assuming bottom navigation)
        (activity as? MainActivity)?.let {
            // Navigate to orders fragment
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupSyncUI() {
        // Update last sync time
        binding.lastSyncText.text = syncRepository.getLastSyncTimeFormatted()
    }

    private fun observeSyncStatus() {
        syncRepository.syncStatus.observe(viewLifecycleOwner) { status ->
            binding.syncStatusText.text = status.message
            binding.syncNowButton.isEnabled = !status.isSyncing
            
            if (status.isSyncing) {
                binding.syncNowButton.text = "⏳ Syncing..."
            } else {
                binding.syncNowButton.text = "☁️ Sync Now"
                binding.lastSyncText.text = syncRepository.getLastSyncTimeFormatted()
            }
        }
    }

    private fun performSync() {
        lifecycleScope.launch {
            val success = syncRepository.sync()
            
            if (success) {
                Toast.makeText(requireContext(), "Sync completed successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Sync failed. Check your connection.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToRegisterCustomer() {
        val registerFragment = RegisterCustomerFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, registerFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToWorkloadConfig() {
        val workloadConfigFragment = WorkloadConfigFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, workloadConfigFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadDashboardStatistics() {
        lifecycleScope.launch {
            try {
                // Get total customers count
                val totalCustomers = database.customerDao().getAllCustomersList().size
                binding.totalCustomersCount.text = totalCustomers.toString()

                // Get total orders count
                val totalOrders = database.orderDao().getAllOrders().size
                binding.totalOrdersCount.text = totalOrders.toString()

                // Get pending orders count
                val pendingOrders = database.orderDao().getAllOrders()
                    .filter { it.status.equals("Pending", ignoreCase = true) || 
                             it.status.equals("In Progress", ignoreCase = true) }
                    .size
                binding.pendingOrdersCount.text = pendingOrders.toString()

                // Get completed orders count
                val completedOrders = database.orderDao().getAllOrders()
                    .filter { it.status.equals("Completed", ignoreCase = true) || 
                             it.status.equals("Delivered", ignoreCase = true) }
                    .size
                binding.completedOrdersCount.text = completedOrders.toString()

            } catch (e: Exception) {
                // Handle error silently or show toast
                Toast.makeText(
                    requireContext(),
                    "Error loading statistics: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadWorkloadStatus() {
        lifecycleScope.launch {
            try {
                val config = database.workloadConfigDao().getConfig() ?: WorkloadConfig()
                val pendingOrders = database.orderDao().getAllOrders().filter { 
                    it.status.equals("Pending", ignoreCase = true) || 
                    it.status.equals("In Progress", ignoreCase = true)
                }
                
                if (pendingOrders.isEmpty()) {
                    // Hide workload card if no pending orders
                    binding.workloadStatusCard.visibility = View.GONE
                    return@launch
                }
                
                val status = WorkloadHelper.calculateWorkloadStatus(pendingOrders, config)
                
                // Show the card
                binding.workloadStatusCard.visibility = View.VISIBLE
                
                // Update UI
                binding.workloadStatusEmoji.text = WorkloadHelper.getStatusEmoji(status.statusLevel)
                binding.workloadPercentage.text = "${status.utilizationPercentage}%"
                binding.workloadProgressBar.progress = status.utilizationPercentage
                binding.workloadMessage.text = status.message
                binding.workloadHours.text = String.format("%.1fh", status.totalHoursNeeded)
                
                if (status.recommendedCapacity > 0) {
                    binding.workloadCapacity.text = "+${status.recommendedCapacity}"
                    binding.workloadCapacity.setTextColor(
                        ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                    )
                } else {
                    binding.workloadCapacity.text = "Full"
                    binding.workloadCapacity.setTextColor(
                        ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                    )
                }
                
                // Update progress bar color based on status
                val progressColor = when (status.statusLevel) {
                    WorkloadHelper.StatusLevel.AVAILABLE -> android.R.color.holo_green_light
                    WorkloadHelper.StatusLevel.BUSY -> android.R.color.holo_orange_light
                    WorkloadHelper.StatusLevel.OVERBOOKED -> android.R.color.holo_red_light
                }
                binding.workloadProgressBar.progressTintList = 
                    ContextCompat.getColorStateList(requireContext(), progressColor)
                    
            } catch (e: Exception) {
                // Hide on error
                binding.workloadStatusCard.visibility = View.GONE
            }
        }
    }
    
    private fun loadDeliveryAlerts() {
        lifecycleScope.launch {
            try {
                val pendingOrders = database.orderDao().getAllOrders().filter { 
                    it.status.equals("Pending", ignoreCase = true) || 
                    it.status.equals("In Progress", ignoreCase = true)
                }
                
                val alerts = WorkloadHelper.getDeliveryAlerts(pendingOrders)
                
                if (alerts.isEmpty()) {
                    binding.deliveryAlertsCard.visibility = View.GONE
                    return@launch
                }
                
                // Show the card
                binding.deliveryAlertsCard.visibility = View.VISIBLE
                
                // Clear existing alerts
                binding.deliveryAlertsContainer.removeAllViews()
                
                // Add alert items (max 5)
                alerts.take(5).forEach { alert ->
                    val alertView = TextView(requireContext()).apply {
                        text = WorkloadHelper.formatDeliveryAlertMessage(alert)
                        textSize = 14f
                        setPadding(0, 8, 0, 8)
                        
                        val textColor = when (alert.alertLevel) {
                            WorkloadHelper.AlertLevel.URGENT -> android.R.color.holo_red_dark
                            WorkloadHelper.AlertLevel.WARNING -> android.R.color.holo_orange_dark
                            WorkloadHelper.AlertLevel.UPCOMING -> android.R.color.holo_blue_dark
                        }
                        setTextColor(ContextCompat.getColor(requireContext(), textColor))
                        
                        // Make clickable to view order details
                        setOnClickListener {
                            navigateToOrderDetail(alert.order.id)
                        }
                    }
                    binding.deliveryAlertsContainer.addView(alertView)
                }
                
            } catch (e: Exception) {
                binding.deliveryAlertsCard.visibility = View.GONE
            }
        }
    }
    
    private fun navigateToOrderDetail(orderId: Int) {
        val orderDetailFragment = OrderDetailFragment.newInstance(orderId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, orderDetailFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun navigateToPendingPayments() {
        // Navigate to orders fragment filtered by unpaid/partial status
        (activity as? MainActivity)?.let {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    private fun loadFinancialDashboard() {
        lifecycleScope.launch {
            try {
                val allOrders = database.orderDao().getAllOrders()
                
                // Calculate today's revenue (fully paid orders today)
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val today = dateFormat.format(java.util.Date())
                
                val todayRevenue = allOrders
                    .filter { it.paymentDate == today && it.paymentStatus == Order.PAYMENT_PAID }
                    .sumOf { it.amount }
                
                // Calculate this month's revenue
                val calendar = java.util.Calendar.getInstance()
                val currentMonth = calendar.get(java.util.Calendar.MONTH)
                val currentYear = calendar.get(java.util.Calendar.YEAR)
                
                val monthRevenue = allOrders
                    .filter { order ->
                        order.paymentStatus == Order.PAYMENT_PAID &&
                        order.paymentDate?.let { date ->
                            try {
                                val paymentCal = java.util.Calendar.getInstance()
                                paymentCal.time = dateFormat.parse(date) ?: return@let false
                                paymentCal.get(java.util.Calendar.MONTH) == currentMonth &&
                                paymentCal.get(java.util.Calendar.YEAR) == currentYear
                            } catch (e: Exception) {
                                false
                            }
                        } ?: false
                    }
                    .sumOf { it.amount }
                
                // Calculate outstanding payments
                val outstandingOrders = allOrders.filter { 
                    it.paymentStatus != Order.PAYMENT_PAID 
                }
                val totalOutstanding = outstandingOrders.sumOf { it.outstandingAmount }
                
                // Update UI
                binding.todayRevenue.text = "₹${String.format("%.2f", todayRevenue)}"
                binding.monthRevenue.text = "₹${String.format("%.2f", monthRevenue)}"
                binding.outstandingAmount.text = "₹${String.format("%.2f", totalOutstanding)}"
                binding.pendingPaymentCount.text = "${outstandingOrders.size} orders"
                
                // Show/hide financial card based on data
                if (allOrders.isEmpty()) {
                    binding.financialDashboardCard.visibility = View.GONE
                } else {
                    binding.financialDashboardCard.visibility = View.VISIBLE
                }
                
            } catch (e: Exception) {
                // Hide on error
                binding.financialDashboardCard.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh greeting and quote when returning to home
        setupGreetingAndQuote()
        // Refresh statistics when returning to home
        loadDashboardStatistics()
        // Refresh workload status
        loadWorkloadStatus()
        loadDeliveryAlerts()
        // Refresh financial dashboard
        loadFinancialDashboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

