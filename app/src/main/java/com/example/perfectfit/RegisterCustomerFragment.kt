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

class RegisterCustomerFragment : Fragment() {

    private var _binding: FragmentRegisterCustomerBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
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

    private fun setupDatePicker() {
        binding.birthDateInput.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.birthDateInput.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun setupButtons() {
        binding.registerButton.setOnClickListener {
            registerCustomer()
        }

        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun registerCustomer() {
        val firstName = binding.firstNameInput.text.toString().trim()
        val lastName = binding.lastNameInput.text.toString().trim()
        val address = binding.addressInput.text.toString().trim()
        val mobile = binding.mobileInput.text.toString().trim()
        val alternateMobile = binding.alternateMobileInput.text.toString().trim()
        val birthDate = binding.birthDateInput.text.toString().trim()

        // Validation
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
        if (birthDate.isEmpty()) {
            binding.birthDateLayout.error = "Birth date is required"
            return
        }

        // Clear errors
        binding.firstNameLayout.error = null
        binding.lastNameLayout.error = null
        binding.addressLayout.error = null
        binding.mobileLayout.error = null
        binding.birthDateLayout.error = null

        // Create customer object
        val customer = Customer(
            firstName = firstName,
            lastName = lastName,
            address = address,
            mobile = mobile,
            alternateMobile = alternateMobile,
            birthDate = birthDate
        )

        // Save to database
        lifecycleScope.launch {
            try {
                database.customerDao().insertCustomer(customer)
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Customer registered successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().onBackPressed()
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error registering customer: ${e.message}",
                        Toast.LENGTH_SHORT
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

