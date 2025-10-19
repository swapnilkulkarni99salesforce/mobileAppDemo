package com.example.perfectfit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentRegisterCustomerBinding
import com.example.perfectfit.models.Customer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment for registering new customers.
 * 
 * This fragment provides a form for entering customer information including:
 * - Name (first and last)
 * - Address
 * - Primary and alternate mobile numbers
 * - Date of birth (with date picker)
 * 
 * Features:
 * - Input validation for all required fields
 * - Date picker dialog for birth date selection
 * - Duplicate customer detection using composite key (firstName, lastName, mobile)
 * - Error handling with user-friendly messages
 * - Modern navigation using FragmentManager instead of deprecated onBackPressed
 * 
 * Validation Rules:
 * - All fields except alternate mobile are required
 * - Mobile number must be at least 10 digits
 * - Duplicate check prevents creating customers with same name and mobile combination
 */
class RegisterCustomerFragment : Fragment() {

    private var _binding: FragmentRegisterCustomerBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    
    // Calendar instance for date picker, initialized to current date
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterCustomerBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()
        setupButtons()
    }

    /**
     * Sets up the date picker for the birth date input field.
     * Clicking the field shows a DatePickerDialog initialized to the current date.
     * Selected date is formatted as dd/MM/yyyy.
     */
    private fun setupDatePicker() {
        binding.birthDateInput.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // Update calendar with selected date
                    calendar.set(year, month, dayOfMonth)
                    
                    // Format and display the selected date
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.birthDateInput.setText(dateFormat.format(calendar.time))
                },
                // Initialize with current date
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    /**
     * Sets up click listeners for the register and cancel buttons.
     */
    private fun setupButtons() {
        binding.registerButton.setOnClickListener {
            registerCustomer()
        }

        binding.cancelButton.setOnClickListener {
            // Use modern navigation approach instead of deprecated onBackPressed
            // This pops the current fragment from the back stack
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Validates and registers a new customer.
     * 
     * Process:
     * 1. Extract and trim all input values
     * 2. Validate required fields
     * 3. Check for duplicate customers using composite key
     * 4. Create and insert customer record
     * 5. Navigate back on success
     * 
     * Validation includes:
     * - Non-empty required fields
     * - Mobile number minimum length (10 digits)
     * - Duplicate prevention using (firstName, lastName, mobile) composite key
     */
    private fun registerCustomer() {
        // Extract input values
        val firstName = binding.firstNameInput.text.toString().trim()
        val lastName = binding.lastNameInput.text.toString().trim()
        val address = binding.addressInput.text.toString().trim()
        val mobile = binding.mobileInput.text.toString().trim()
        val alternateMobile = binding.alternateMobileInput.text.toString().trim()
        val birthDate = binding.birthDateInput.text.toString().trim()

        // ===== Input Validation =====
        if (firstName.isEmpty()) {
            binding.firstNameLayout.error = "First name is required"
            return
        }
        if (lastName.isEmpty()) {
            binding.lastNameLayout.error = "Last name is required"
            return
        }
        if (address.isEmpty()) {
            binding.addressLayout.error = "Address is required"
            return
        }
        if (mobile.isEmpty()) {
            binding.mobileLayout.error = "Mobile number is required"
            return
        }
        if (mobile.length < 10) {
            binding.mobileLayout.error = "Mobile number must be at least 10 digits"
            return
        }
        if (birthDate.isEmpty()) {
            binding.birthDateLayout.error = "Birth date is required"
            return
        }

        // Clear previous error messages
        binding.firstNameLayout.error = null
        binding.lastNameLayout.error = null
        binding.addressLayout.error = null
        binding.mobileLayout.error = null
        binding.birthDateLayout.error = null

        // ===== Database Operations =====
        lifecycleScope.launch {
            try {
                // Check for duplicate customer using composite unique key
                // This prevents creating multiple customers with same name and mobile
                val existingCustomer = database.customerDao().getCustomerByCompositeKey(
                    firstName,
                    lastName,
                    mobile
                )

                if (existingCustomer != null) {
                    // Customer already exists - show error message
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Customer '$firstName $lastName' with mobile $mobile already exists!",
                            Toast.LENGTH_LONG
                        ).show()
                        
                        // Highlight the conflicting fields
                        binding.firstNameLayout.error = "Duplicate customer"
                        binding.lastNameLayout.error = "Duplicate customer"
                        binding.mobileLayout.error = "Duplicate customer"
                    }
                    return@launch
                }

                // Create customer object
                val customer = Customer(
                    firstName = firstName,
                    lastName = lastName,
                    address = address,
                    mobile = mobile,
                    alternateMobile = alternateMobile,
                    birthDate = birthDate
                )

                // Insert customer
                database.customerDao().insertCustomer(customer)
                
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Customer registered successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Use modern navigation approach instead of deprecated onBackPressed
                    parentFragmentManager.popBackStack()
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error registering customer: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

