package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.perfectfit.databinding.FragmentCustomerDetailBinding
import com.example.perfectfit.models.Customer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomerDetailFragment : Fragment() {

    private var _binding: FragmentCustomerDetailBinding? = null
    private val binding get() = _binding!!
    private var customer: Customer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customer = Customer(
                id = it.getInt("id"),
                firstName = it.getString("firstName") ?: "",
                lastName = it.getString("lastName") ?: "",
                address = it.getString("address") ?: "",
                mobile = it.getString("mobile") ?: "",
                alternateMobile = it.getString("alternateMobile") ?: "",
                birthDate = it.getString("birthDate") ?: ""
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customer?.let { displayCustomerDetails(it) }
        setupButtons()
    }

    private fun displayCustomerDetails(customer: Customer) {
        // Display customer name without label
        binding.customerName.text = customer.fullName

        // Calculate and display age
        val age = calculateAge(customer.birthDate)
        binding.customerAge.text = "$age years"
        binding.customerBirthdate.text = customer.birthDate

        // Display mobile
        binding.customerMobile.text = customer.mobile

        // Display alternate mobile if present
        if (customer.alternateMobile.isNotEmpty()) {
            binding.alternateMobileLayout.visibility = View.VISIBLE
            binding.customerAlternateMobile.text = customer.alternateMobile
        } else {
            binding.alternateMobileLayout.visibility = View.GONE
        }

        // Display address if present
        if (customer.address.isNotEmpty()) {
            binding.addressCard.visibility = View.VISIBLE
            binding.customerAddress.text = customer.address
        } else {
            binding.addressCard.visibility = View.GONE
        }
    }

    private fun calculateAge(birthDateStr: String): Int {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val birthDate = dateFormat.parse(birthDateStr)
            
            if (birthDate != null) {
                val birthCalendar = Calendar.getInstance()
                birthCalendar.time = birthDate
                
                val today = Calendar.getInstance()
                
                var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                
                // Check if birthday hasn't occurred yet this year
                if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }
                
                age
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun setupButtons() {
        binding.viewMeasurementsButton.setOnClickListener {
            customer?.let { navigateToFitProfile(it) }
        }
        
        binding.createOrderButton.setOnClickListener {
            customer?.let { navigateToCreateOrder(it) }
        }
        
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    
    private fun navigateToFitProfile(customer: Customer) {
        val fitProfileFragment = ClientFitProfileFragment.newInstance(customer)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fitProfileFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun navigateToCreateOrder(customer: Customer) {
        val createOrderFragment = CreateOrderFragment.newInstance(customer)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, createOrderFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(customer: Customer): CustomerDetailFragment {
            val fragment = CustomerDetailFragment()
            val bundle = Bundle().apply {
                putInt("id", customer.id)
                putString("firstName", customer.firstName)
                putString("lastName", customer.lastName)
                putString("address", customer.address)
                putString("mobile", customer.mobile)
                putString("alternateMobile", customer.alternateMobile)
                putString("birthDate", customer.birthDate)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

