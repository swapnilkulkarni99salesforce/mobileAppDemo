package com.example.perfectfit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.databinding.ItemCustomerBinding
import com.example.perfectfit.models.Customer

class CustomersAdapter(
    customers: List<Customer>,
    private val onItemClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {

    private var originalCustomers: List<Customer> = customers
    private var visibleCustomers: List<Customer> = customers

    inner class CustomerViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(customer: Customer) {
            binding.customerName.text = customer.fullName
            binding.customerEmail.text = customer.address
            binding.customerPhone.text = customer.mobile
            
            // Set content description for avatar
            binding.customerAvatar.contentDescription = 
                binding.root.context.getString(
                    com.example.perfectfit.R.string.cd_customer_avatar,
                    customer.fullName
                )
            
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

    fun updateData(newCustomers: List<Customer>) {
        originalCustomers = newCustomers
        visibleCustomers = newCustomers
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            visibleCustomers = originalCustomers
        } else {
            val qLower = trimmed.lowercase()
            val qDigits = trimmed.filter { it.isDigit() }
            visibleCustomers = originalCustomers.filter { customer ->
                val nameMatch = customer.fullName.lowercase().contains(qLower)
                val mobileNormalized = customer.mobile.filter { it.isDigit() }
                val mobileMatch = qDigits.isNotEmpty() && mobileNormalized.contains(qDigits)
                nameMatch || mobileMatch
            }
        }
        notifyDataSetChanged()
    }
}

