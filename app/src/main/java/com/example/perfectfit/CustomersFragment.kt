package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perfectfit.adapters.CustomersAdapter
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentCustomersBinding
import com.example.perfectfit.models.Customer

class CustomersFragment : Fragment() {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var adapter: CustomersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomersBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCustomers()
    }

    private fun setupRecyclerView() {
        adapter = CustomersAdapter(emptyList()) { customer ->
            navigateToCustomerDetail(customer)
        }
        binding.customersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@CustomersFragment.adapter
        }
    }

    private fun observeCustomers() {
        database.customerDao().getAllCustomers().observe(viewLifecycleOwner) { customers ->
            adapter = CustomersAdapter(customers) { customer ->
                navigateToCustomerDetail(customer)
            }
            binding.customersRecyclerView.adapter = adapter
        }
    }

    private fun navigateToCustomerDetail(customer: Customer) {
        val detailFragment = CustomerDetailFragment.newInstance(customer)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

