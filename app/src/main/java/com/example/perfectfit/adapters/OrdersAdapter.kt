package com.example.perfectfit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.databinding.ItemOrderBinding
import com.example.perfectfit.models.Order

class OrdersAdapter(
    private val orders: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.orderId.text = order.orderId
            binding.orderCustomer.text = "Customer: ${order.customerName}"
            binding.orderType.text = "Type: ${order.orderType}"
            binding.orderDate.text = "Order Date: ${order.orderDate}"
            binding.orderDeliveryDate.text = "Delivery: ${order.estimatedDeliveryDate}"
            binding.orderAmount.text = order.formattedAmount
            binding.orderStatus.text = order.status
            
            binding.root.setOnClickListener {
                onOrderClick(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size
}

