package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentOrderDetailBinding
import com.example.perfectfit.models.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private var order: Order? = null
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        
        arguments?.let {
            val orderId = it.getInt("orderId")
            loadOrder(orderId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupStatusDropdown()
        setupListeners()
    }

    private fun loadOrder(orderId: Int) {
        lifecycleScope.launch {
            try {
                val loadedOrder = withContext(Dispatchers.IO) {
                    database.orderDao().getOrderById(orderId)
                }
                
                withContext(Dispatchers.Main) {
                    loadedOrder?.let {
                        order = it
                        displayOrderDetails(it)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error loading order: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun displayOrderDetails(order: Order) {
        binding.orderIdText.text = order.orderId
        binding.detailCustomerName.text = order.customerName
        binding.detailOrderType.text = order.orderType
        binding.detailOrderDate.text = order.orderDate
        binding.detailDeliveryDate.text = order.estimatedDeliveryDate
        binding.detailAmount.text = order.formattedAmount
        binding.statusDropdown.setText(order.status, false)
        
        if (order.instructions.isNotEmpty()) {
            binding.detailInstructions.text = order.instructions
        } else {
            binding.detailInstructions.text = "No special instructions"
        }
    }

    private fun setupStatusDropdown() {
        val statuses = arrayOf("Pending", "In Progress", "Completed", "Closed")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statuses)
        binding.statusDropdown.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.updateStatusButton.setOnClickListener {
            updateOrderStatus()
        }
        
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun updateOrderStatus() {
        val newStatus = binding.statusDropdown.text.toString()
        
        if (newStatus.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a status", Toast.LENGTH_SHORT).show()
            return
        }
        
        order?.let { currentOrder ->
            val updatedOrder = currentOrder.copy(status = newStatus)
            
            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        database.orderDao().update(updatedOrder)
                    }
                    
                    withContext(Dispatchers.Main) {
                        order = updatedOrder
                        Toast.makeText(requireContext(), "Order status updated to: $newStatus", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error updating status: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(orderId: Int): OrderDetailFragment {
            val fragment = OrderDetailFragment()
            val bundle = Bundle().apply {
                putInt("orderId", orderId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

