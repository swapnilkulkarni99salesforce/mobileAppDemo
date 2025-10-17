package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.databinding.FragmentHomeBinding
import com.example.perfectfit.sync.SyncRepository
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var syncRepository: SyncRepository

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
        syncRepository = SyncRepository(requireContext())
        setupClickListeners()
        setupSyncUI()
        observeSyncStatus()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

