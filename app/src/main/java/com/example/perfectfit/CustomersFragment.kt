package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perfectfit.adapters.CustomersAdapter
import com.example.perfectfit.databinding.FragmentCustomersBinding
import com.example.perfectfit.models.Customer

class CustomersFragment : Fragment() {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val customers = getSampleCustomers()
        val adapter = CustomersAdapter(customers)
        
        binding.customersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    private fun getSampleCustomers(): List<Customer> {
        return listOf(
            Customer(1, "John Doe", "john.doe@email.com", "+1 234 567 8901"),
            Customer(2, "Jane Smith", "jane.smith@email.com", "+1 234 567 8902"),
            Customer(3, "Michael Johnson", "michael.j@email.com", "+1 234 567 8903"),
            Customer(4, "Emily Davis", "emily.davis@email.com", "+1 234 567 8904"),
            Customer(5, "David Wilson", "david.wilson@email.com", "+1 234 567 8905"),
            Customer(6, "Sarah Brown", "sarah.brown@email.com", "+1 234 567 8906"),
            Customer(7, "James Martinez", "james.m@email.com", "+1 234 567 8907"),
            Customer(8, "Lisa Anderson", "lisa.anderson@email.com", "+1 234 567 8908")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

