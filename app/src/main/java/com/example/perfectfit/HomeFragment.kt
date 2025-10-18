package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentHomeBinding
import com.example.perfectfit.sync.SyncRepository
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

    override fun onResume() {
        super.onResume()
        // Refresh greeting and quote when returning to home
        setupGreetingAndQuote()
        // Refresh statistics when returning to home
        loadDashboardStatistics()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

