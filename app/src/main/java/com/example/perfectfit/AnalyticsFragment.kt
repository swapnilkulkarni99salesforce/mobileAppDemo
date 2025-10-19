package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentAnalyticsBinding
import com.example.perfectfit.models.Customer
import com.example.perfectfit.models.Order
import com.example.perfectfit.models.ProductionStage
import com.example.perfectfit.models.WorkloadConfig
import com.example.perfectfit.utils.WorkloadHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment displaying business analytics and insights.
 * 
 * Features:
 * - Customer Lifetime Value (CLV) rankings
 * - Production bottleneck identification
 * - Capacity utilization status
 * - Slow-moving order alerts
 * - Upcoming birthday reminders
 * 
 * Data Sources:
 * - Customer CLV from Order.amount totals
 * - Production stages from ProductionStage table
 * - Workload from active orders
 * - Birthday data from Customer.birthDate
 * 
 * @see [Customer] for CLV tracking
 * @see [ProductionStage] for bottleneck data
 * @see [WorkloadHelper] for capacity calculations
 */
class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        loadAnalytics()
    }

    private fun setupListeners() {
        binding.refreshButton.setOnClickListener {
            loadAnalytics()
        }
    }

    /**
     * Loads all analytics data.
     */
    private fun loadAnalytics() {
        lifecycleScope.launch {
            loadTopCustomers()
            loadProductionBottlenecks()
            loadCapacityStatus()
            loadUpcomingBirthdays()
        }
    }

    /**
     * Loads and displays top customers by lifetime value.
     */
    private suspend fun loadTopCustomers() {
        try {
            val customers = withContext(Dispatchers.IO) {
                database.customerDao().getAllCustomersList()
            }
            
            // Sort by CLV and take top 10
            val topCustomers = customers
                .sortedByDescending { it.totalOrdersValue }
                .take(10)
                .filter { it.totalOrdersValue > 0 }
            
            withContext(Dispatchers.Main) {
                displayTopCustomers(topCustomers)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Displays top customers list.
     */
    private fun displayTopCustomers(customers: List<Customer>) {
        binding.topCustomersContainer.removeAllViews()
        
        if (customers.isEmpty()) {
            val textView = TextView(requireContext()).apply {
                text = "No customer data available yet"
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
            binding.topCustomersContainer.addView(textView)
            return
        }
        
        customers.forEachIndexed { index, customer ->
            val itemView = LayoutInflater.from(requireContext())
                .inflate(android.R.layout.simple_list_item_2, binding.topCustomersContainer, false)
            
            val title = itemView.findViewById<TextView>(android.R.id.text1)
            val subtitle = itemView.findViewById<TextView>(android.R.id.text2)
            
            val rank = when (index) {
                0 -> "🥇"
                1 -> "🥈"
                2 -> "🥉"
                else -> "${index + 1}."
            }
            
            title.text = "$rank ${customer.fullName}"
            title.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            
            subtitle.text = "CLV: ${customer.getFormattedLifetimeValue()}"
            subtitle.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
            
            binding.topCustomersContainer.addView(itemView)
        }
    }

    /**
     * Loads and displays production bottlenecks and slow orders.
     */
    private suspend fun loadProductionBottlenecks() {
        try {
            val allStages = withContext(Dispatchers.IO) {
                database.productionStageDao().getAllActiveStages()
            }
            
            val slowOrders = withContext(Dispatchers.IO) {
                val threshold = System.currentTimeMillis() - (24 * 3600000) // 24 hours
                database.orderStageHistoryDao().getSlowMovingOrders(threshold)
            }
            
            withContext(Dispatchers.Main) {
                displayBottlenecks(allStages, slowOrders.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Displays bottleneck information.
     */
    private fun displayBottlenecks(stages: List<ProductionStage>, slowOrderCount: Int) {
        binding.bottlenecksContainer.removeAllViews()
        
        // Group stages by type
        val stageGroups = stages.groupBy { it.currentStage }
        
        var hasBottlenecks = false
        
        ProductionStage.getAllStages().forEach { stageName ->
            val count = stageGroups[stageName]?.size ?: 0
            
            if (count > 3) { // Threshold for bottleneck
                hasBottlenecks = true
                val textView = TextView(requireContext()).apply {
                    text = "⚠️ $count orders in ${ProductionStage().copy(currentStage = stageName).getStageDisplayName()}"
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    setPadding(0, 8, 0, 8)
                }
                binding.bottlenecksContainer.addView(textView)
            }
        }
        
        // Slow orders
        if (slowOrderCount > 0) {
            hasBottlenecks = true
            val textView = TextView(requireContext()).apply {
                text = "🐌 $slowOrderCount orders moving slowly (>24h in stage)"
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
                setPadding(0, 8, 0, 8)
            }
            binding.bottlenecksContainer.addView(textView)
        }
        
        if (!hasBottlenecks) {
            val textView = TextView(requireContext()).apply {
                text = "✅ No bottlenecks detected - Production flowing smoothly!"
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
            }
            binding.bottlenecksContainer.addView(textView)
        }
    }

    /**
     * Loads and displays capacity utilization status.
     */
    private suspend fun loadCapacityStatus() {
        try {
            val activeOrders = withContext(Dispatchers.IO) {
                val stages = database.productionStageDao().getAllActiveStages()
                stages.mapNotNull { stage ->
                    database.orderDao().getOrderById(stage.orderId)
                }
            }
            
            val config = withContext(Dispatchers.IO) {
                database.workloadConfigDao().getConfig() ?: WorkloadConfig()
            }
            
            val status = WorkloadHelper.calculateWorkloadStatus(activeOrders, config)
            
            withContext(Dispatchers.Main) {
                displayCapacityStatus(status, config)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Displays capacity status with recommendations.
     */
    private fun displayCapacityStatus(
        status: WorkloadHelper.WorkloadStatus,
        config: WorkloadConfig
    ) {
        binding.capacityProgress.progress = status.utilizationPercentage
        
        when {
            status.utilizationPercentage >= 90 -> {
                binding.capacityStatus.text = "Critical - ${status.utilizationPercentage}% Capacity"
                binding.capacityStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                )
                binding.capacityRecommendation.text = "⚠️ At maximum capacity! Consider:\n" +
                        "• Extending delivery dates\n" +
                        "• Hiring temporary help\n" +
                        "• Prioritizing high-value orders"
            }
            status.utilizationPercentage >= 75 -> {
                binding.capacityStatus.text = "High - ${status.utilizationPercentage}% Capacity"
                binding.capacityStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark)
                )
                binding.capacityRecommendation.text = "🔥 Running at high capacity\n" +
                        "Can accept ${status.recommendedCapacity} more orders"
            }
            status.utilizationPercentage >= 50 -> {
                binding.capacityStatus.text = "Optimal - ${status.utilizationPercentage}% Capacity"
                binding.capacityStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                )
                binding.capacityRecommendation.text = "✅ Running smoothly\n" +
                        "Can accept ${status.recommendedCapacity} more orders"
            }
            else -> {
                binding.capacityStatus.text = "Low - ${status.utilizationPercentage}% Capacity"
                binding.capacityStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
                )
                binding.capacityRecommendation.text = "💡 Capacity available!\n" +
                        "Can accept ${status.recommendedCapacity} more orders\n" +
                        "Consider running promotions"
            }
        }
    }

    /**
     * Loads and displays upcoming birthdays (next 30 days).
     */
    private suspend fun loadUpcomingBirthdays() {
        try {
            val customers = withContext(Dispatchers.IO) {
                database.customerDao().getAllCustomersList()
            }
            
            val upcomingBirthdays = customers.filter { customer ->
                isUpcomingBirthday(customer.birthDate, 30)
            }.sortedBy { customer ->
                getDaysUntilBirthday(customer.birthDate)
            }
            
            withContext(Dispatchers.Main) {
                displayUpcomingBirthdays(upcomingBirthdays)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Checks if a birthday is within the next specified days.
     */
    private fun isUpcomingBirthday(birthDate: String, withinDays: Int): Boolean {
        return try {
            val daysUntil = getDaysUntilBirthday(birthDate)
            daysUntil in 0..withinDays
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Calculates days until next birthday.
     */
    private fun getDaysUntilBirthday(birthDate: String): Int {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val birth = format.parse(birthDate) ?: return Int.MAX_VALUE
            
            val birthCal = Calendar.getInstance().apply { time = birth }
            val today = Calendar.getInstance()
            
            // Set birthday to this year
            val nextBirthday = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, birthCal.get(Calendar.DAY_OF_MONTH))
                set(Calendar.MONTH, birthCal.get(Calendar.MONTH))
                set(Calendar.YEAR, today.get(Calendar.YEAR))
            }
            
            // If birthday already passed this year, use next year
            if (nextBirthday.before(today)) {
                nextBirthday.add(Calendar.YEAR, 1)
            }
            
            val diffMs = nextBirthday.timeInMillis - today.timeInMillis
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
            
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }

    /**
     * Displays upcoming birthdays list.
     */
    private fun displayUpcomingBirthdays(customers: List<Customer>) {
        binding.birthdaysContainer.removeAllViews()
        
        if (customers.isEmpty()) {
            val textView = TextView(requireContext()).apply {
                text = "No birthdays in the next 30 days"
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
            binding.birthdaysContainer.addView(textView)
            return
        }
        
        customers.take(10).forEach { customer ->
            val daysUntil = getDaysUntilBirthday(customer.birthDate)
            
            val textView = TextView(requireContext()).apply {
                text = when (daysUntil) {
                    0 -> "🎂 TODAY: ${customer.fullName}"
                    1 -> "🎂 Tomorrow: ${customer.fullName}"
                    else -> "🎂 In $daysUntil days: ${customer.fullName}"
                }
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                setPadding(0, 8, 0, 8)
            }
            
            binding.birthdaysContainer.addView(textView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): AnalyticsFragment {
            return AnalyticsFragment()
        }
    }
}

