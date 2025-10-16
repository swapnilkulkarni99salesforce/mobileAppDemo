package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perfectfit.adapters.OrdersAdapter
import com.example.perfectfit.databinding.FragmentOrdersBinding
import com.example.perfectfit.models.Order

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupRecyclerView() {
        val orders = getSampleOrders()
        val adapter = OrdersAdapter(orders)
        
        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    private fun getSampleOrders(): List<Order> {
        return listOf(
            Order(1, "Order #12345", "John Doe", "Oct 16, 2025", "$99.99", "Pending"),
            Order(2, "Order #12346", "Jane Smith", "Oct 15, 2025", "$149.99", "Completed"),
            Order(3, "Order #12347", "Michael Johnson", "Oct 14, 2025", "$79.99", "Pending"),
            Order(4, "Order #12348", "Emily Davis", "Oct 13, 2025", "$199.99", "Shipped"),
            Order(5, "Order #12349", "David Wilson", "Oct 12, 2025", "$59.99", "Completed"),
            Order(6, "Order #12350", "Sarah Brown", "Oct 11, 2025", "$129.99", "Processing"),
            Order(7, "Order #12351", "James Martinez", "Oct 10, 2025", "$89.99", "Pending"),
            Order(8, "Order #12352", "Lisa Anderson", "Oct 09, 2025", "$169.99", "Completed")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

