package com.example.perfectfit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.databinding.ItemCustomerBinding
import com.example.perfectfit.models.Customer

class CustomersAdapter(private val customers: List<Customer>) :
    RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {

    inner class CustomerViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(customer: Customer) {
            binding.customerName.text = customer.name
            binding.customerEmail.text = customer.email
            binding.customerPhone.text = customer.phone
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
        holder.bind(customers[position])
    }

    override fun getItemCount(): Int = customers.size
}

