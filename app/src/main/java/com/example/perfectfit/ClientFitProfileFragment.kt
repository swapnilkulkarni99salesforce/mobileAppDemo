package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentClientFitProfileBinding
import com.example.perfectfit.models.Customer
import com.example.perfectfit.models.Measurement
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClientFitProfileFragment : Fragment() {

    private var _binding: FragmentClientFitProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private var customer: Customer? = null
    private var measurement: Measurement? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        arguments?.let {
            val customerId = it.getInt("customerId")
            customer = Customer(
                id = customerId,
                firstName = it.getString("firstName") ?: "",
                lastName = it.getString("lastName") ?: "",
                address = it.getString("address") ?: "",
                mobile = it.getString("mobile") ?: "",
                alternateMobile = it.getString("alternateMobile") ?: "",
                birthDate = it.getString("birthDate") ?: ""
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientFitProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        customer?.let {
            binding.clientName.text = it.fullName
            setupViewPager()
            loadMeasurements(it.id)
        }
    }

    private fun setupViewPager() {
        val adapter = MeasurementPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "KURTI"
                1 -> "PANT"
                2 -> "BLOUSE"
                else -> ""
            }
        }.attach()
    }

    private fun loadMeasurements(customerId: Int) {
        database.measurementDao().getMeasurementByCustomerId(customerId)
            .observe(viewLifecycleOwner) { measurement ->
                this.measurement = measurement
                updateLastUpdatedText(measurement?.lastUpdated)
            }
    }

    private fun updateLastUpdatedText(timestamp: Long?) {
        if (timestamp != null) {
            val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            binding.lastUpdatedText.text = "Last Updated: ${sdf.format(Date(timestamp))}"
        } else {
            binding.lastUpdatedText.text = "No measurements recorded"
        }
    }

    fun getMeasurement(): Measurement? = measurement

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class MeasurementPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> KurtiMeasurementsFragment()
                1 -> PantMeasurementsFragment()
                2 -> BlouseMeasurementsFragment()
                else -> Fragment()
            }
        }
    }

    companion object {
        fun newInstance(customer: Customer): ClientFitProfileFragment {
            val fragment = ClientFitProfileFragment()
            val bundle = Bundle().apply {
                putInt("customerId", customer.id)
                putString("firstName", customer.firstName)
                putString("lastName", customer.lastName)
                putString("address", customer.address)
                putString("mobile", customer.mobile)
                putString("alternateMobile", customer.alternateMobile)
                putString("birthDate", customer.birthDate)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

