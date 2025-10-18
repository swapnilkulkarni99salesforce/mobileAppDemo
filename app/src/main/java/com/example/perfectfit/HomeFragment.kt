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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var syncRepository: SyncRepository
    private lateinit var database: AppDatabase

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
        setupClickListeners()
        setupSyncUI()
        observeSyncStatus()
        loadDashboardStatistics()
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
        // Refresh statistics when returning to home
        loadDashboardStatistics()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

