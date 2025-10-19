package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.addTextChangedListener
import com.example.perfectfit.adapters.CustomersAdapter
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentCustomersBinding
import com.example.perfectfit.models.Customer

class CustomersFragment : Fragment() {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var adapter: CustomersAdapter
    private var allCustomers: List<Customer> = emptyList()
    private var lastQuery: String = ""

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
        setupSearch()
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
            allCustomers = customers
            adapter.updateData(customers)
            // Re-apply current query so filtered state persists after data updates
            adapter.filter(lastQuery)
        }
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener { editable ->
            lastQuery = editable?.toString() ?: ""
            adapter.filter(lastQuery)
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

