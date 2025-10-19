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
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment(), NewActionBottomSheet.NewActionListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var syncRepository: SyncRepository
    private lateinit var database: AppDatabase
    
    // Financial data visibility
    private var isFinancialDataVisible = false
    private var actualTodayRevenue = 0.0
    private var actualMonthRevenue = 0.0
    private var actualPendingAmount = 0.0
    private var actualPendingCount = 0
    
    private val motivationalQuotes = listOf(
        "Success is the sum of small efforts repeated day in and day out.",
        "Quality is not an act, it is a habit.",
        "Excellence is not a skill, it's an attitude.",
        "Great things are done by a series of small things brought together.",
        "Perfection is not attainable, but if we chase perfection we can catch excellence.",
        "The difference between ordinary and extraordinary is that little extra.",
        "Every order you complete is a masterpiece in the making.",
        "Your dedication to quality makes every customer smile.",
        "Craftsmanship is the foundation of success.",
        "Take pride in your work, one stitch at a time."
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
        
        setupGreeting()
        setupClickListeners()
        setupSyncUI()
        observeSyncStatus()
        loadDashboardStatistics()
        loadWorkloadStatus()
        loadDeliveryAlerts()
        loadFinancialDashboard()
    }

    private fun setupGreeting() {
        val greeting = getTimeBasedGreeting()
        binding.greetingText.text = greeting.first
        binding.subtitleText.text = greeting.second
        
        // Set random motivational quote
        val randomQuote = motivationalQuotes.random()
        binding.motivationalQuoteText.text = randomQuote
    }

    private fun getTimeBasedGreeting(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hourOfDay) {
            in 0..4 -> Pair(
                getString(R.string.greeting_night),
                getString(R.string.subtitle_night)
            )
            in 5..11 -> Pair(
                getString(R.string.greeting_morning),
                getString(R.string.subtitle_morning)
            )
            in 12..16 -> Pair(
                getString(R.string.greeting_afternoon),
                getString(R.string.subtitle_afternoon)
            )
            in 17..20 -> Pair(
                getString(R.string.greeting_evening),
                getString(R.string.subtitle_evening)
            )
            else -> Pair(
                getString(R.string.greeting_night),
                getString(R.string.subtitle_night)
            )
        }
    }

    private fun setupClickListeners() {
        // Extended FAB click
        binding.fabNew.setOnClickListener {
            showNewActionBottomSheet()
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
                Toast.makeText(requireContext(), R.string.auto_sync_enabled, Toast.LENGTH_SHORT).show()
            } else {
                syncRepository.disableBackgroundSync()
                Toast.makeText(requireContext(), R.string.auto_sync_disabled, Toast.LENGTH_SHORT).show()
            }
        }

        binding.viewPendingPaymentsButton.setOnClickListener {
            navigateToPendingPayments()
        }
        
        // Financial visibility toggle
        binding.toggleFinancialVisibility.setOnClickListener {
            toggleFinancialVisibility()
        }
    }

    private fun showNewActionBottomSheet() {
        val bottomSheet = NewActionBottomSheet.newInstance()
        bottomSheet.setListener(this)
        bottomSheet.show(parentFragmentManager, NewActionBottomSheet.TAG)
    }

    override fun onNewCustomer() {
        navigateToRegisterCustomer()
    }

    override fun onNewOrder() {
        navigateToCreateOrder()
    }

    private fun navigateToCreateOrder() {
        val createOrderFragment = CreateOrderFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, createOrderFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupSyncUI() {
        val lastSync = syncRepository.getLastSyncTimeFormatted()
        binding.lastSyncText.text = if (lastSync.isEmpty()) {
            getString(R.string.sync_never)
        } else {
            getString(R.string.sync_last, lastSync)
        }
    }

    private fun observeSyncStatus() {
        syncRepository.syncStatus.observe(viewLifecycleOwner) { status ->
            binding.syncStatusText.text = status.message
            binding.syncNowButton.isEnabled = !status.isSyncing

            if (status.isSyncing) {
                binding.syncNowButton.text = getString(R.string.sync_in_progress)
            } else {
                binding.syncNowButton.text = getString(R.string.action_sync)
                setupSyncUI()
            }
        }
    }

    private fun performSync() {
        lifecycleScope.launch {
            val success = syncRepository.sync()

            val message = if (success) {
                // Announce to screen readers
                binding.root.announceForAccessibility(getString(R.string.announce_sync_complete))
                getString(R.string.sync_success)
            } else {
                getString(R.string.sync_failed)
            }
            
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    private fun navigateToPendingPayments() {
        (activity as? MainActivity)?.let {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadDashboardStatistics() {
        lifecycleScope.launch {
            try {
                val totalCustomers = database.customerDao().getAllCustomersList().size
                binding.totalCustomersCount.text = totalCustomers.toString()

                val totalOrders = database.orderDao().getAllOrders().size
                binding.totalOrdersCount.text = totalOrders.toString()

                val pendingOrders = database.orderDao().getAllOrders()
                    .filter {
                        it.status.equals("Pending", ignoreCase = true) ||
                                it.status.equals("In Progress", ignoreCase = true)
                    }
                    .size
                binding.pendingOrdersCount.text = pendingOrders.toString()

                val completedOrders = database.orderDao().getAllOrders()
                    .filter {
                        it.status.equals("Completed", ignoreCase = true) ||
                                it.status.equals("Delivered", ignoreCase = true)
                    }
                    .size
                binding.completedOrdersCount.text = completedOrders.toString()

                // Announce to screen readers
                binding.root.announceForAccessibility(getString(R.string.announce_data_loaded))

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_loading),
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
                    binding.workloadStatusCard.visibility = View.GONE
                    return@launch
                }

                val status = WorkloadHelper.calculateWorkloadStatus(pendingOrders, config)

                binding.workloadStatusCard.visibility = View.VISIBLE
                binding.workloadPercentage.text = "${status.utilizationPercentage}%"
                binding.workloadProgressBar.progress = status.utilizationPercentage
                binding.workloadMessage.text = status.message

                // Update progress bar color based on status
                val progressColor = when (status.statusLevel) {
                    WorkloadHelper.StatusLevel.AVAILABLE -> R.color.status_completed
                    WorkloadHelper.StatusLevel.BUSY -> R.color.status_pending
                    WorkloadHelper.StatusLevel.OVERBOOKED -> R.color.status_error
                }
                
                binding.workloadProgressBar.setIndicatorColor(
                    ContextCompat.getColor(requireContext(), progressColor)
                )

                // Announce to screen readers
                binding.workloadStatusCard.announceForAccessibility(
                    getString(R.string.announce_workload, status.message)
                )

            } catch (e: Exception) {
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
                        setPadding(0, 12, 0, 12)

                        val textColor = when (alert.alertLevel) {
                            WorkloadHelper.AlertLevel.URGENT -> R.color.status_error
                            WorkloadHelper.AlertLevel.WARNING -> R.color.status_pending
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

    private fun loadFinancialDashboard() {
        lifecycleScope.launch {
            try {
                val allOrders = database.orderDao().getAllOrders()

                // Calculate today's revenue
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

                // Store actual values
                actualTodayRevenue = todayRevenue
                actualMonthRevenue = monthRevenue
                actualPendingAmount = totalOutstanding
                actualPendingCount = outstandingOrders.size

                // Update UI with masked values initially
                updateFinancialUI()

                if (allOrders.isEmpty()) {
                    binding.financialDashboardCard.visibility = View.GONE
                } else {
                    binding.financialDashboardCard.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                binding.financialDashboardCard.visibility = View.GONE
            }
        }
    }
    
    private fun toggleFinancialVisibility() {
        isFinancialDataVisible = !isFinancialDataVisible
        updateFinancialUI()
        
        // Update icon
        val iconRes = if (isFinancialDataVisible) {
            android.R.drawable.ic_menu_view // Eye open
        } else {
            android.R.drawable.ic_secure // Eye closed/lock
        }
        binding.toggleFinancialVisibility.setIconResource(iconRes)
        
        // Announce change for accessibility
        val announcement = if (isFinancialDataVisible) {
            "Financial data visible"
        } else {
            "Financial data hidden"
        }
        binding.toggleFinancialVisibility.announceForAccessibility(announcement)
    }
    
    private fun updateFinancialUI() {
        if (isFinancialDataVisible) {
            // Show actual values
            binding.todayRevenue.text = "₹${String.format("%.2f", actualTodayRevenue)}"
            binding.monthRevenue.text = "₹${String.format("%.2f", actualMonthRevenue)}"
            binding.outstandingAmount.text = "₹${String.format("%.2f", actualPendingAmount)}"
            binding.pendingPaymentCount.text = getString(R.string.orders_count, actualPendingCount)
        } else {
            // Show masked values
            binding.todayRevenue.text = "₹••••"
            binding.monthRevenue.text = "₹••••"
            binding.outstandingAmount.text = "₹••••"
            binding.pendingPaymentCount.text = "•• orders"
        }
    }

    override fun onResume() {
        super.onResume()
        setupGreeting()
        loadDashboardStatistics()
        loadWorkloadStatus()
        loadDeliveryAlerts()
        loadFinancialDashboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
