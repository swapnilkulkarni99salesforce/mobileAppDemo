package com.example.perfectfit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.databinding.ItemCustomerBinding
import com.example.perfectfit.models.Customer

/**
 * RecyclerView adapter for displaying a list of customers.
 * 
 * This adapter implements efficient list updates using DiffUtil instead of
 * notifyDataSetChanged(), which results in better performance and smooth animations
 * when the list changes.
 * 
 * Features:
 * - Search/filter functionality by customer name or mobile number
 * - Click handling for navigation to customer details
 * - Accessibility support with content descriptions
 * - ViewBinding for type-safe view access
 * 
 * Performance Optimizations:
 * - Uses DiffUtil for efficient RecyclerView updates
 * - Only updates changed items instead of entire list
 * - Maintains separate filtered and unfiltered lists for fast search
 * 
 * @param customers Initial list of customers
 * @param onItemClick Callback invoked when a customer item is clicked
 */
class CustomersAdapter(
    customers: List<Customer>,
    private val onItemClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {

    // Full unfiltered list of customers
    private var originalCustomers: List<Customer> = customers
    
    // Currently visible customers (may be filtered)
    private var visibleCustomers: List<Customer> = customers

    /**
     * ViewHolder for individual customer items.
     * Uses ViewBinding for type-safe view access.
     */
    inner class CustomerViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds customer data to the views.
         * @param customer The customer to display
         */
        fun bind(customer: Customer) {
            binding.customerName.text = customer.fullName
            binding.customerEmail.text = customer.address
            binding.customerPhone.text = customer.mobile
            
            // Set content description for accessibility (screen readers)
            binding.customerAvatar.contentDescription = 
                binding.root.context.getString(
                    com.example.perfectfit.R.string.cd_customer_avatar,
                    customer.fullName
                )
            
            // Set click listener for entire item
            binding.root.setOnClickListener {
                onItemClick(customer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(visibleCustomers[position])
    }

    override fun getItemCount(): Int = visibleCustomers.size

    /**
     * Updates the adapter with a new list of customers.
     * Uses DiffUtil to calculate the difference and dispatch efficient updates.
     * 
     * DiffUtil Benefits:
     * - Only updates changed items (better performance)
     * - Smooth animations for insertions, deletions, and moves
     * - No unnecessary rebinds of unchanged items
     * 
     * @param newCustomers The new list of customers
     */
    fun updateData(newCustomers: List<Customer>) {
        val oldCustomers = originalCustomers
        originalCustomers = newCustomers
        
        // Calculate differences between old and new lists
        val diffResult = DiffUtil.calculateDiff(CustomerDiffCallback(oldCustomers, newCustomers))
        
        // Update visible list if not currently filtered
        if (visibleCustomers == oldCustomers) {
            visibleCustomers = newCustomers
        }
        
        // Dispatch the updates efficiently
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Filters the customer list based on a search query.
     * Searches by customer name (case-insensitive) and mobile number (digits only).
     * 
     * Search Logic:
     * - Name: Case-insensitive partial match
     * - Mobile: Digits-only partial match (ignores spaces, hyphens, etc.)
     * - Empty query: Shows all customers
     * 
     * Performance Note: Uses DiffUtil for smooth filtering animations.
     * 
     * @param query The search query string
     */
    fun filter(query: String) {
        val oldVisible = visibleCustomers
        val trimmed = query.trim()
        
        visibleCustomers = if (trimmed.isEmpty()) {
            // No filter: show all customers
            originalCustomers
        } else {
            // Apply filter
            val qLower = trimmed.lowercase()
            val qDigits = trimmed.filter { it.isDigit() }
            
            originalCustomers.filter { customer ->
                // Check name match (case-insensitive)
                val nameMatch = customer.fullName.lowercase().contains(qLower)
                
                // Check mobile match (digits only)
                val mobileNormalized = customer.mobile.filter { it.isDigit() }
                val mobileMatch = qDigits.isNotEmpty() && mobileNormalized.contains(qDigits)
                
                nameMatch || mobileMatch
            }
        }
        
        // Calculate and dispatch differences for smooth animations
        val diffResult = DiffUtil.calculateDiff(CustomerDiffCallback(oldVisible, visibleCustomers))
        diffResult.dispatchUpdatesTo(this)
    }
    
    /**
     * DiffUtil.Callback implementation for calculating differences between customer lists.
     * 
     * This class defines how to compare customers for determining if they are the same item
     * and if their contents are the same. DiffUtil uses this to efficiently update the RecyclerView.
     */
    private class CustomerDiffCallback(
        private val oldList: List<Customer>,
        private val newList: List<Customer>
    ) : DiffUtil.Callback() {
        
        override fun getOldListSize(): Int = oldList.size
        
        override fun getNewListSize(): Int = newList.size
        
        /**
         * Checks if two items represent the same customer.
         * Uses the customer ID as the unique identifier.
         */
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        
        /**
         * Checks if the contents of two customers are identical.
         * This is called only if areItemsTheSame() returns true.
         * 
         * Uses data class equality which compares all properties.
         */
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldCustomer = oldList[oldItemPosition]
            val newCustomer = newList[newItemPosition]
            
            // Compare all relevant fields for display
            return oldCustomer.firstName == newCustomer.firstName &&
                   oldCustomer.lastName == newCustomer.lastName &&
                   oldCustomer.mobile == newCustomer.mobile &&
                   oldCustomer.address == newCustomer.address
        }
    }
}

