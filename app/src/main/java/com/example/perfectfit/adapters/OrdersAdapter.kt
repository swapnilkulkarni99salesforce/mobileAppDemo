package com.example.perfectfit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.databinding.ItemOrderBinding
import com.example.perfectfit.models.Order

/**
 * RecyclerView adapter for displaying a list of orders.
 * 
 * This adapter implements efficient list updates using DiffUtil for smooth animations
 * and better performance when the order list changes (e.g., status updates, new orders).
 * 
 * Features:
 * - Click handling for navigation to order details
 * - ViewBinding for type-safe view access
 * - Efficient updates using DiffUtil
 * - Displays order summary information (ID, customer, dates, amount, status)
 * 
 * @param orders Initial list of orders
 * @param onOrderClick Callback invoked when an order item is clicked
 */
class OrdersAdapter(
    orders: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    // Mutable list of orders for efficient updates
    private var ordersList: List<Order> = orders

    /**
     * ViewHolder for individual order items.
     * Uses ViewBinding for type-safe view access.
     */
    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds order data to the views.
         * @param order The order to display
         */
        fun bind(order: Order) {
            binding.orderId.text = order.orderId
            binding.orderCustomer.text = "Customer: ${order.customerName}"
            binding.orderType.text = "Type: ${order.orderType}"
            binding.orderDate.text = "Order Date: ${order.orderDate}"
            binding.orderDeliveryDate.text = "Delivery: ${order.estimatedDeliveryDate}"
            binding.orderAmount.text = order.formattedAmount
            binding.orderStatus.text = order.status
            
            // Set click listener for entire item
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
        holder.bind(ordersList[position])
    }

    override fun getItemCount(): Int = ordersList.size
    
    /**
     * Updates the adapter with a new list of orders.
     * Uses DiffUtil to calculate the difference and dispatch efficient updates.
     * 
     * DiffUtil Benefits:
     * - Only updates changed items (better performance)
     * - Smooth animations for insertions, deletions, and moves
     * - Preserves item animations when status changes
     * - No unnecessary rebinds of unchanged items
     * 
     * Common use cases:
     * - Order list refreshed from database
     * - Order status updated (Pending -> In Progress -> Completed)
     * - Payment status updated
     * - New order added
     * 
     * @param newOrders The new list of orders
     */
    fun updateOrders(newOrders: List<Order>) {
        val oldOrders = ordersList
        ordersList = newOrders
        
        // Calculate differences between old and new lists
        val diffResult = DiffUtil.calculateDiff(OrderDiffCallback(oldOrders, newOrders))
        
        // Dispatch the updates efficiently
        diffResult.dispatchUpdatesTo(this)
    }
    
    /**
     * DiffUtil.Callback implementation for calculating differences between order lists.
     * 
     * This class defines how to compare orders for determining if they are the same item
     * and if their contents are the same. DiffUtil uses this to efficiently update the RecyclerView.
     */
    private class OrderDiffCallback(
        private val oldList: List<Order>,
        private val newList: List<Order>
    ) : DiffUtil.Callback() {
        
        override fun getOldListSize(): Int = oldList.size
        
        override fun getNewListSize(): Int = newList.size
        
        /**
         * Checks if two items represent the same order.
         * Uses the order ID as the unique identifier.
         */
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        
        /**
         * Checks if the contents of two orders are identical.
         * This is called only if areItemsTheSame() returns true.
         * 
         * Compares all fields that affect the UI display:
         * - Customer name
         * - Order type
         * - Dates
         * - Amount
         * - Status (important for showing status changes)
         * - Payment status (important for payment tracking)
         */
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldOrder = oldList[oldItemPosition]
            val newOrder = newList[newItemPosition]
            
            // Compare all relevant fields for display
            return oldOrder.customerName == newOrder.customerName &&
                   oldOrder.orderType == newOrder.orderType &&
                   oldOrder.orderDate == newOrder.orderDate &&
                   oldOrder.estimatedDeliveryDate == newOrder.estimatedDeliveryDate &&
                   oldOrder.amount == newOrder.amount &&
                   oldOrder.status == newOrder.status &&
                   oldOrder.paymentStatus == newOrder.paymentStatus
        }
    }
}

