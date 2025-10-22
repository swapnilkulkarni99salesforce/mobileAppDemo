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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import com.google.android.material.color.MaterialColors
import com.google.android.material.textview.MaterialTextView

/**
 * Home Dashboard Fragment for the Perfect Fit tailoring application.
 * 
 * This is the main landing screen that provides an overview of the business including:
 * - Personalized time-based greetings
 * - Motivational quotes for daily inspiration
 * - Business statistics (customers, orders, completion rates)
 * - Workload status and capacity indicators
 * - Delivery alerts for upcoming/overdue orders
 * - Financial dashboard with revenue tracking
 * - Synchronization controls
 * - Quick action buttons for common tasks
 * 
 * Architecture:
 * - MVVM-inspired pattern with LiveData observation
 * - Coroutines for asynchronous operations
 * - Repository pattern for data access
 * - ViewBinding for type-safe view access
 * 
 * Key Features:
 * 
 * 1. Greeting System:
 *    - Time-based greetings (Morning, Afternoon, Evening, Night)
 *    - Context-appropriate subtitles
 *    - Random motivational quotes
 * 
 * 2. Dashboard Statistics:
 *    - Total customers count
 *    - Total orders count
 *    - Pending/In-progress orders
 *    - Completed orders
 *    - Auto-refreshes on resume
 * 
 * 3. Workload Management:
 *    - Real-time capacity utilization (%)
 *    - Visual progress indicator with color coding:
 *      * Green: Available (< 70%)
 *      * Yellow: Busy (70-90%)
 *      * Red: Overbooked (> 90%)
 *    - Configurable working hours
 *    - Visibility toggles based on pending orders
 * 
 * 4. Delivery Alerts:
 *    - Urgent: Overdue or due today (red)
 *    - Warning: Due within 2-3 days (orange)
 *    - Upcoming: Due within 7 days (blue)
 *    - Click to view order details
 *    - Shows top 5 alerts only
 * 
 * 5. Financial Dashboard:
 *    - Today's revenue
 *    - This month's revenue
 *    - Outstanding payments
 *    - Pending payment count
 *    - Privacy toggle to hide/show amounts
 * 
 * 6. Synchronization:
 *    - Manual sync button
 *    - Auto-sync toggle switch
 *    - Last sync timestamp display
 *    - Sync status messages
 * 
 * 7. Quick Actions (FAB):
 *    - New Customer registration
 *    - New Order creation
 *    - Opens bottom sheet modal
 * 
 * Performance Optimizations:
 * - Efficient data loading with suspend functions
 * - LiveData for reactive updates
 * - Try-catch blocks for graceful error handling
 * - Visibility management to hide empty sections
 * - Accessibility announcements for screen readers
 * 
 * Accessibility Features:
 * - Content descriptions for all interactive elements
 * - Announcements for data updates
 * - Semantic markup with MaterialTextView
 * - High contrast for critical alerts
 * 
 * @see [NewActionBottomSheet] for quick action sheet
 * @see [SyncRepository] for synchronization logic
 * @see [WorkloadHelper] for workload calculations
 */
class HomeFragment : Fragment(), NewActionBottomSheet.NewActionListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var syncRepository: SyncRepository
    private lateinit var database: AppDatabase
    
    // ===== Financial Data Privacy Management =====
    // These fields manage the visibility of sensitive financial information
    private var isFinancialDataVisible = false
    private var actualTodayRevenue = 0.0
    private var actualMonthRevenue = 0.0
    private var actualPendingAmount = 0.0
    private var actualPendingCount = 0
    
    // ===== Motivational Quotes =====
    // Randomly displayed quotes to inspire the user
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
        loadWeeklyCapacity()  // ✨ QUICK WIN 3: Weekly capacity view
        loadFinancialDashboard()
    }

    /**
     * Sets up personalized greeting based on current time of day.
     * Also displays a random motivational quote for user inspiration.
     */
    private fun setupGreeting() {
        val greeting = getTimeBasedGreeting()
        binding.greetingText.text = greeting.first
        binding.subtitleText.text = greeting.second
        
        // Display a random motivational quote
        val randomQuote = motivationalQuotes.random()
        binding.motivationalQuoteText.text = randomQuote
    }

    /**
     * Determines appropriate greeting and subtitle based on current time.
     * 
     * Time Ranges:
     * - Night (0:00-4:59): Midnight greeting
     * - Morning (5:00-11:59): Morning greeting
     * - Afternoon (12:00-16:59): Afternoon greeting
     * - Evening (17:00-20:59): Evening greeting
     * - Night (21:00-23:59): Night greeting
     * 
     * @return Pair of (greeting, subtitle) strings
     */
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
        
        // Quick Access Cards
        binding.quickAccessAnalytics.setOnClickListener {
            navigateToAnalytics()
        }
        
        binding.quickAccessPortfolio.setOnClickListener {
            navigateToPortfolio()
        }
        
        binding.quickAccessWorkload.setOnClickListener {
            navigateToWorkloadConfig()
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

        binding.viewAllOrdersButton.setOnClickListener {
            navigateToOrders()
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
    
    private fun navigateToAnalytics() {
        val analyticsFragment = AnalyticsFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, analyticsFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun navigateToPortfolio() {
        val portfolioFragment = PortfolioFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, portfolioFragment)
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

    private fun navigateToOrders() {
        (activity as? MainActivity)?.let {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    /**
     * Loads and displays dashboard statistics.
     * 
     * Statistics include:
     * - Total customers registered
     * - Total orders placed
     * - Pending/In-progress orders
     * - Completed/Delivered orders
     * 
     * Updates UI with counts and announces changes for accessibility.
     * Gracefully handles errors with user-friendly error messages.
     */
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

    /**
     * Loads and displays current workload status.
     * 
     * Calculates workload utilization based on:
     * - Number of pending orders
     * - Time required per order (from configuration)
     * - Available working hours this week
     * 
     * Visual Indicators:
     * - Progress bar showing utilization percentage
     * - Color coding: Green (< 70%), Yellow (70-90%), Red (> 90%)
     * - Message indicating current capacity status
     * 
     * Card is hidden if no pending orders exist.
     * Announces status changes for accessibility.
     */
    private fun loadWorkloadStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val config = database.workloadConfigDao().getConfig() ?: WorkloadConfig()
                val pendingOrders = database.orderDao().getAllOrders().filter {
                    it.status.equals("Pending", ignoreCase = true) ||
                            it.status.equals("In Progress", ignoreCase = true)
                }

                // Check if view is still attached before accessing binding
                if (!isAdded || _binding == null) return@launch

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

                // ✨ NEW: Update capacity status badge
                binding.capacityStatusBadge.visibility = View.VISIBLE
                val emoji = WorkloadHelper.getStatusEmoji(status.statusLevel)
                binding.capacityStatusText.text = "$emoji ${status.utilizationPercentage}% Capacity"

            } catch (e: Exception) {
                // Check if view is still attached before accessing binding
                if (isAdded && _binding != null) {
                    binding.workloadStatusCard.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Loads and displays delivery alerts for upcoming and overdue orders.
     * 
     * Alert Levels:
     * - URGENT (Red): Overdue or due today
     * - WARNING (Orange): Due within 2-3 days
     * - UPCOMING (Blue): Due within 7 days
     * 
     * Features:
     * - Displays top 5 alerts only
     * - Color-coded labels for quick visual scanning
     * - Click on alert navigates to order detail
     * - Ripple effect for better affordance
     * - High contrast text on error container background
     * 
     * Card is hidden if no alerts exist.
     */
    private fun loadDeliveryAlerts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val pendingOrders = database.orderDao().getAllOrders().filter {
                    it.status.equals("Pending", ignoreCase = true) ||
                            it.status.equals("In Progress", ignoreCase = true)
                }

                val alerts = WorkloadHelper.getDeliveryAlerts(pendingOrders)

                // Check if view is still attached before accessing binding
                if (!isAdded || _binding == null) return@launch

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
                    // Check again before each UI operation
                    if (!isAdded || _binding == null) return@launch
                    
                    // Build styled message with a colored severity label for visibility
                    val labelText = when (alert.alertLevel) {
                        WorkloadHelper.AlertLevel.URGENT -> "URGENT"
                        WorkloadHelper.AlertLevel.WARNING -> "Warning"
                        WorkloadHelper.AlertLevel.UPCOMING -> "Upcoming"
                    }
                    val message = WorkloadHelper.formatDeliveryAlertMessage(alert)
                    val combined = SpannableString("$labelText • $message")

                    val labelColorRes = when (alert.alertLevel) {
                        WorkloadHelper.AlertLevel.URGENT -> R.color.status_error
                        WorkloadHelper.AlertLevel.WARNING -> R.color.status_pending
                        WorkloadHelper.AlertLevel.UPCOMING -> android.R.color.holo_blue_dark
                    }
                    combined.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), labelColorRes)),
                        0,
                        labelText.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // Use high-contrast text color against error container
                    val onErrorTextColor = MaterialColors.getColor(
                        binding.deliveryAlertsCard,
                        com.google.android.material.R.attr.colorOnErrorContainer
                    )

                    // Padding in dp
                    val padV = (12 * resources.displayMetrics.density).toInt()
                    val padH = (16 * resources.displayMetrics.density).toInt()

                    val alertView = MaterialTextView(requireContext()).apply {
                        text = combined
                        textSize = 15f
                        ellipsize = TextUtils.TruncateAt.END
                        maxLines = 2
                        setPadding(padH, padV, padH, padV)
                        setTextColor(onErrorTextColor)

                        // Ripple background for better affordance
                        val outValue = TypedValue()
                        requireContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                        setBackgroundResource(outValue.resourceId)

                        // Click to view order details
                        setOnClickListener {
                            navigateToOrderDetail(alert.order.id)
                        }
                    }

                    binding.deliveryAlertsContainer.addView(alertView)
                }

            } catch (e: Exception) {
                // Check if view is still attached before accessing binding
                if (isAdded && _binding != null) {
                    binding.deliveryAlertsCard.visibility = View.GONE
                }
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
    
    /**
     * ✨ QUICK WIN 3: Loads and displays 4-week capacity outlook
     * 
     * Shows capacity planning for the next 4 weeks:
     * - Week number and date range
     * - Utilization percentage
     * - Order count
     * - Status indicator (Available/Busy/Overbooked)
     * - Recommendations for best time to schedule
     * 
     * Helps with forward planning and identifying capacity bottlenecks
     */
    /**
     * ✨ UI UPDATE: Now displays in visible card!
     */
    private fun loadWeeklyCapacity() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val config = database.workloadConfigDao().getConfig() ?: WorkloadConfig()
                val allOrders = database.orderDao().getAllOrders()
                
                val weeklyData = WorkloadHelper.calculateMultiWeekCapacity(allOrders, config, weeksAhead = 4)
                
                // Check if view is still attached before accessing binding
                if (!isAdded || _binding == null) return@launch
                
                // Hide card if no data
                if (weeklyData.isEmpty()) {
                    binding.weeklyCapacityCard.visibility = View.GONE
                    return@launch
                }
                
                // Format and display the summary
                val summaryText = WorkloadHelper.formatWeeklySummary(weeklyData)
                
                withContext(Dispatchers.Main) {
                    // Double check after context switch
                    if (!isAdded || _binding == null) return@withContext
                    
                    // ✨ SHOW THE CARD!
                    binding.weeklyCapacityCard.visibility = View.VISIBLE
                    binding.weeklyCapacityText.text = summaryText
                    
                    // View Details button - show detailed dialog
                    binding.viewCapacityDetailsButton.setOnClickListener {
                        showCapacityDetailsDialog(weeklyData)
                    }
                }
                
            } catch (e: Exception) {
                // Silently fail - not critical for app functionality
                if (isAdded && _binding != null) {
                    binding.weeklyCapacityCard.visibility = View.GONE
                }
            }
        }
    }
    
    /**
     * ✨ NEW: Show detailed capacity dialog
     */
    private fun showCapacityDetailsDialog(weeklyData: List<WorkloadHelper.WeeklyCapacity>) {
        val dateFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
        
        val detailedText = buildString {
            append("📅 Detailed Capacity Breakdown\n\n")
            
            weeklyData.forEach { week ->
                val startStr = dateFormat.format(week.weekStartDate.time)
                val endStr = dateFormat.format(week.weekEndDate.time)
                val emoji = WorkloadHelper.getStatusEmoji(week.statusLevel)
                val current = if (week.isCurrentWeek) " (THIS WEEK)" else ""
                
                append("Week ${week.weekNumber}$current\n")
                append("$startStr - $endStr\n")
                append("$emoji ${week.utilizationPercentage}% Capacity\n")
                append("${week.orderCount} orders scheduled\n")
                append("${String.format("%.1f", week.allocatedHours)}h / ${String.format("%.1f", week.totalAvailableHours)}h\n")
                
                when (week.statusLevel) {
                    WorkloadHelper.StatusLevel.AVAILABLE -> append("✨ Great time for new orders!\n")
                    WorkloadHelper.StatusLevel.BUSY -> append("⚠️ High capacity - limited availability\n")
                    WorkloadHelper.StatusLevel.OVERBOOKED -> append("🚨 OVERBOOKED - Consider rescheduling\n")
                }
                
                append("\n")
            }
            
            // Recommendation
            val bestWeek = weeklyData.minByOrNull { it.utilizationPercentage }
            bestWeek?.let {
                append("💡 Best time for new orders: Week ${it.weekNumber}\n")
                append("   Only ${it.utilizationPercentage}% booked!")
            }
        }
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("📊 Capacity Details")
            .setMessage(detailedText)
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Loads and displays financial dashboard with revenue tracking.
     * 
     * Metrics Calculated:
     * - Today's Revenue: Orders marked as PAID today
     * - This Month's Revenue: Orders marked as PAID this month
     * - Outstanding Amount: Sum of unpaid and partially paid orders
     * - Pending Payment Count: Number of orders with outstanding balance
     * 
     * Privacy Features:
     * - Amounts initially masked as "₹••••"
     * - Toggle button to show/hide actual values
     * - Icon changes based on visibility state
     * 
     * Card is hidden if no orders exist.
     */
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
    
    /**
     * Toggles the visibility of financial data for privacy.
     * 
     * Behavior:
     * - Masked: Shows "₹••••" for all amounts
     * - Visible: Shows actual rupee values
     * 
     * Updates:
     * - Toggle button icon (eye open/closed)
     * - Accessibility announcement for screen readers
     * - All financial text views
     */
    private fun toggleFinancialVisibility() {
        isFinancialDataVisible = !isFinancialDataVisible
        updateFinancialUI()
        
        // Update toggle icon based on visibility state
        val iconRes = if (isFinancialDataVisible) {
            android.R.drawable.ic_menu_view // Eye open icon
        } else {
            android.R.drawable.ic_secure // Lock/eye closed icon
        }
        binding.toggleFinancialVisibility.setIconResource(iconRes)
        
        // Announce visibility change for accessibility
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

    /**
     * Called when fragment becomes visible to user.
     * Refreshes all dashboard data to ensure current information is displayed.
     * 
     * Refreshes:
     * - Time-based greeting (in case time period changed)
     * - Dashboard statistics
     * - Workload status
     * - Delivery alerts
     * - Financial dashboard
     */
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
