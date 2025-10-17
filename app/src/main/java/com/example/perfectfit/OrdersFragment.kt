package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perfectfit.adapters.OrdersAdapter
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentOrdersBinding
import com.example.perfectfit.models.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadOrders()
    }

    override fun onResume() {
        super.onResume()
        // Reload orders when fragment is resumed (e.g., after creating a new order)
        loadOrders()
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter(emptyList())
        
        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ordersAdapter
        }
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            try {
                val orders = withContext(Dispatchers.IO) {
                    database.orderDao().getAllOrders()
                }
                
                withContext(Dispatchers.Main) {
                    if (orders.isEmpty()) {
                        binding.emptyStateText.visibility = View.VISIBLE
                        binding.ordersRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyStateText.visibility = View.GONE
                        binding.ordersRecyclerView.visibility = View.VISIBLE
                        ordersAdapter = OrdersAdapter(orders)
                        binding.ordersRecyclerView.adapter = ordersAdapter
                    }
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

