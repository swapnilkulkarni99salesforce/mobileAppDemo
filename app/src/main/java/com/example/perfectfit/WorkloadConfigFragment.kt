package com.example.perfectfit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentWorkloadConfigBinding
import com.example.perfectfit.models.WorkloadConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkloadConfigFragment : Fragment() {

    private var _binding: FragmentWorkloadConfigBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkloadConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        
        loadExistingConfig()
        setupListeners()
        setupTextWatchers()
    }

    private fun loadExistingConfig() {
        lifecycleScope.launch {
            try {
                val config = withContext(Dispatchers.IO) {
                    database.workloadConfigDao().getConfig() ?: WorkloadConfig()
                }
                
                withContext(Dispatchers.Main) {
                    populateFields(config)
                    updateSummary()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error loading configuration: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun populateFields(config: WorkloadConfig) {
        binding.timePerOrderInput.setText(config.timePerOrderHours.toString())
        binding.mondayHoursInput.setText(config.mondayHours.toString())
        binding.tuesdayHoursInput.setText(config.tuesdayHours.toString())
        binding.wednesdayHoursInput.setText(config.wednesdayHours.toString())
        binding.thursdayHoursInput.setText(config.thursdayHours.toString())
        binding.fridayHoursInput.setText(config.fridayHours.toString())
        binding.saturdayHoursInput.setText(config.saturdayHours.toString())
        binding.sundayHoursInput.setText(config.sundayHours.toString())
    }

    private fun setupListeners() {
        binding.saveConfigButton.setOnClickListener {
            saveConfiguration()
        }

        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        // ✨ QUICK WIN 2: Add extra hours quick action
        binding.addExtraHoursButton.setOnClickListener {
            showAddExtraHoursDialog()
        }
    }
    
    /**
     * ✨ NEW: Show dialog to add extra hours for today
     */
    private fun showAddExtraHoursDialog() {
        val input = android.widget.EditText(requireContext()).apply {
            hint = "Extra hours (e.g., 2.5)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(50, 40, 50, 40)
        }
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("⏰ Add Extra Hours Today")
            .setMessage("How many extra hours can you work today?\n\nThis will temporarily increase your capacity.")
            .setView(input)
            .setPositiveButton("Preview Impact") { _, _ ->
                val hours = input.text.toString().toFloatOrNull()
                if (hours != null && hours > 0) {
                    showImpactPreview(hours)
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    /**
     * ✨ NEW: Show impact preview before applying
     */
    private fun showImpactPreview(extraHours: Float) {
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
                
                // Calculate impact
                val impact = com.example.perfectfit.utils.QuickActionsHelper.calculateExtraHoursImpact(
                    extraHours,
                    config,
                    pendingOrders.size
                )
                
                val impactMessage = com.example.perfectfit.utils.QuickActionsHelper.formatImpactMessage(impact)
                
                // Show impact and confirm
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("📊 Impact Preview")
                    .setMessage(impactMessage)
                    .setPositiveButton("Apply Now") { _, _ ->
                        applyExtraHours(extraHours)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error calculating impact: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    /**
     * ✨ NEW: Apply the extra hours
     */
    private fun applyExtraHours(hours: Float) {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    com.example.perfectfit.utils.QuickActionsHelper.addExtraHoursToday(
                        requireContext(),
                        hours,
                        database
                    )
                }
                
                result.onSuccess { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    // Reload the configuration to show updated hours
                    loadExistingConfig()
                }.onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error applying extra hours: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSummary()
            }
        }

        binding.timePerOrderInput.addTextChangedListener(textWatcher)
        binding.mondayHoursInput.addTextChangedListener(textWatcher)
        binding.tuesdayHoursInput.addTextChangedListener(textWatcher)
        binding.wednesdayHoursInput.addTextChangedListener(textWatcher)
        binding.thursdayHoursInput.addTextChangedListener(textWatcher)
        binding.fridayHoursInput.addTextChangedListener(textWatcher)
        binding.saturdayHoursInput.addTextChangedListener(textWatcher)
        binding.sundayHoursInput.addTextChangedListener(textWatcher)
    }

    private fun updateSummary() {
        try {
            val timePerOrder = binding.timePerOrderInput.text.toString().toFloatOrNull() ?: 2.0f
            val mondayHours = binding.mondayHoursInput.text.toString().toFloatOrNull() ?: 0f
            val tuesdayHours = binding.tuesdayHoursInput.text.toString().toFloatOrNull() ?: 0f
            val wednesdayHours = binding.wednesdayHoursInput.text.toString().toFloatOrNull() ?: 0f
            val thursdayHours = binding.thursdayHoursInput.text.toString().toFloatOrNull() ?: 0f
            val fridayHours = binding.fridayHoursInput.text.toString().toFloatOrNull() ?: 0f
            val saturdayHours = binding.saturdayHoursInput.text.toString().toFloatOrNull() ?: 0f
            val sundayHours = binding.sundayHoursInput.text.toString().toFloatOrNull() ?: 0f

            val totalWeeklyHours = mondayHours + tuesdayHours + wednesdayHours + 
                                   thursdayHours + fridayHours + saturdayHours + sundayHours

            val ordersPerWeek = if (timePerOrder > 0) {
                (totalWeeklyHours / timePerOrder).toInt()
            } else {
                0
            }

            binding.summaryText.text = """
                Total weekly hours: $totalWeeklyHours hours
                Orders per week: $ordersPerWeek orders
                Time per order: $timePerOrder hours
            """.trimIndent()
        } catch (e: Exception) {
            // Ignore parsing errors during typing
        }
    }

    private fun saveConfiguration() {
        try {
            val timePerOrder = binding.timePerOrderInput.text.toString().toFloatOrNull()
            val mondayHours = binding.mondayHoursInput.text.toString().toFloatOrNull()
            val tuesdayHours = binding.tuesdayHoursInput.text.toString().toFloatOrNull()
            val wednesdayHours = binding.wednesdayHoursInput.text.toString().toFloatOrNull()
            val thursdayHours = binding.thursdayHoursInput.text.toString().toFloatOrNull()
            val fridayHours = binding.fridayHoursInput.text.toString().toFloatOrNull()
            val saturdayHours = binding.saturdayHoursInput.text.toString().toFloatOrNull()
            val sundayHours = binding.sundayHoursInput.text.toString().toFloatOrNull()

            // Validation
            if (timePerOrder == null || timePerOrder <= 0) {
                binding.timePerOrderLayout.error = "Please enter a valid time per order"
                return
            }

            if (mondayHours == null || mondayHours < 0) {
                binding.mondayHoursLayout.error = "Please enter valid hours"
                return
            }

            if (tuesdayHours == null || tuesdayHours < 0) {
                binding.tuesdayHoursLayout.error = "Please enter valid hours"
                return
            }

            if (wednesdayHours == null || wednesdayHours < 0) {
                binding.wednesdayHoursLayout.error = "Please enter valid hours"
                return
            }

            if (thursdayHours == null || thursdayHours < 0) {
                binding.thursdayHoursLayout.error = "Please enter valid hours"
                return
            }

            if (fridayHours == null || fridayHours < 0) {
                binding.fridayHoursLayout.error = "Please enter valid hours"
                return
            }

            if (saturdayHours == null || saturdayHours < 0) {
                binding.saturdayHoursLayout.error = "Please enter valid hours"
                return
            }

            if (sundayHours == null || sundayHours < 0) {
                binding.sundayHoursLayout.error = "Please enter valid hours"
                return
            }

            val config = WorkloadConfig(
                timePerOrderHours = timePerOrder,
                mondayHours = mondayHours,
                tuesdayHours = tuesdayHours,
                wednesdayHours = wednesdayHours,
                thursdayHours = thursdayHours,
                fridayHours = fridayHours,
                saturdayHours = saturdayHours,
                sundayHours = sundayHours
            )

            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        // Delete old config and insert new one
                        database.workloadConfigDao().deleteAllConfigs()
                        database.workloadConfigDao().insertConfig(config)
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Configuration saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Error saving configuration: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

