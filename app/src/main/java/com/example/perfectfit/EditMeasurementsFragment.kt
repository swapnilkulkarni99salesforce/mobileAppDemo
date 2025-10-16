package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.perfectfit.adapters.EditableMeasurementAdapter
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.databinding.FragmentEditMeasurementsBinding
import com.example.perfectfit.models.Customer
import com.example.perfectfit.models.EditableMeasurementField
import com.example.perfectfit.models.Measurement
import kotlinx.coroutines.launch

class EditMeasurementsFragment : Fragment() {

    private var _binding: FragmentEditMeasurementsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private var customer: Customer? = null
    private var existingMeasurement: Measurement? = null
    private var selectedTab: String = "KURTI" // KURTI, PANT, or BLOUSE
    
    private val kurtiFields = mutableListOf<EditableMeasurementField>()
    private val pantFields = mutableListOf<EditableMeasurementField>()
    private val blouseFields = mutableListOf<EditableMeasurementField>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(requireContext())
        
        arguments?.let {
            val customerId = it.getInt("customerId")
            selectedTab = it.getString("selectedTab") ?: "KURTI"
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
        _binding = FragmentEditMeasurementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set dynamic title based on selected tab
        val title = when (selectedTab) {
            "KURTI" -> "Edit Kurti Measurements"
            "PANT" -> "Edit Pant Measurements"
            "BLOUSE" -> "Edit Blouse Measurements"
            else -> "Edit Measurements"
        }
        binding.editTitle.text = title
        
        customer?.let {
            binding.customerNameText.text = it.fullName
            loadExistingMeasurements(it.id)
        }
        
        setupButtons()
    }

    private fun loadExistingMeasurements(customerId: Int) {
        lifecycleScope.launch {
            existingMeasurement = database.measurementDao().getMeasurementByCustomerIdSync(customerId)
            initializeFields()
            setupRecyclerViews()
        }
    }

    private fun initializeFields() {
        val m = existingMeasurement
        
        // Kurti Fields
        kurtiFields.clear()
        kurtiFields.addAll(listOf(
            EditableMeasurementField("Kurti Length", m?.kurtiLength ?: "", "kurtiLength"),
            EditableMeasurementField("Full Shoulder", m?.fullShoulder ?: "", "fullShoulder"),
            EditableMeasurementField("Upper Chest Round", m?.upperChestRound ?: "", "upperChestRound"),
            EditableMeasurementField("Chest Round", m?.chestRound ?: "", "chestRound"),
            EditableMeasurementField("Waist Round", m?.waistRound ?: "", "waistRound"),
            EditableMeasurementField("Shoulder to Apex", m?.shoulderToApex ?: "", "shoulderToApex"),
            EditableMeasurementField("Apex to Apex", m?.apexToApex ?: "", "apexToApex"),
            EditableMeasurementField("Shoulder to Low Chest Length", m?.shoulderToLowChestLength ?: "", "shoulderToLowChestLength"),
            EditableMeasurementField("Skap Length", m?.skapLength ?: "", "skapLength"),
            EditableMeasurementField("Skap Length Round", m?.skapLengthRound ?: "", "skapLengthRound"),
            EditableMeasurementField("Hip Round", m?.hipRound ?: "", "hipRound"),
            EditableMeasurementField("Front Neck Deep", m?.frontNeckDeep ?: "", "frontNeckDeep"),
            EditableMeasurementField("Front Neck Width", m?.frontNeckWidth ?: "", "frontNeckWidth"),
            EditableMeasurementField("Back Neck Deep", m?.backNeckDeep ?: "", "backNeckDeep"),
            EditableMeasurementField("Ready Shoulder", m?.readyShoulder ?: "", "readyShoulder"),
            EditableMeasurementField("Sleeves Height (Short)", m?.sleevesHeightShort ?: "", "sleevesHeightShort"),
            EditableMeasurementField("Sleeves Height (Elbow)", m?.sleevesHeightElbow ?: "", "sleevesHeightElbow"),
            EditableMeasurementField("Sleeves Height (3/4th)", m?.sleevesHeightThreeQuarter ?: "", "sleevesHeightThreeQuarter"),
            EditableMeasurementField("Sleeves Round", m?.sleevesRound ?: "", "sleevesRound")
        ))
        
        // Pant Fields
        pantFields.clear()
        pantFields.addAll(listOf(
            EditableMeasurementField("Waist", m?.pantWaist ?: "", "pantWaist"),
            EditableMeasurementField("Length", m?.pantLength ?: "", "pantLength"),
            EditableMeasurementField("Hip", m?.pantHip ?: "", "pantHip"),
            EditableMeasurementField("Bottom", m?.pantBottom ?: "", "pantBottom")
        ))
        
        // Blouse Fields
        blouseFields.clear()
        blouseFields.add(
            EditableMeasurementField("Blouse Length", m?.blouseLength ?: "", "blouseLength")
        )
    }

    private fun setupRecyclerViews() {
        requireActivity().runOnUiThread {
            // Show/hide sections based on selected tab
            when (selectedTab) {
                "KURTI" -> {
                    binding.kurtiSection.visibility = View.VISIBLE
                    binding.pantSection.visibility = View.GONE
                    binding.blouseSection.visibility = View.GONE
                    binding.kurtiFieldsRecycler.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = EditableMeasurementAdapter(kurtiFields)
                    }
                }
                "PANT" -> {
                    binding.kurtiSection.visibility = View.GONE
                    binding.pantSection.visibility = View.VISIBLE
                    binding.blouseSection.visibility = View.GONE
                    binding.pantFieldsRecycler.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = EditableMeasurementAdapter(pantFields)
                    }
                }
                "BLOUSE" -> {
                    binding.kurtiSection.visibility = View.GONE
                    binding.pantSection.visibility = View.GONE
                    binding.blouseSection.visibility = View.VISIBLE
                    binding.blouseFieldsRecycler.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = EditableMeasurementAdapter(blouseFields)
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            saveMeasurements()
        }
        
        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun saveMeasurements() {
        customer?.let { cust ->
            lifecycleScope.launch {
                try {
                    val measurement = Measurement(
                        id = existingMeasurement?.id ?: 0,
                        customerId = cust.id,
                        // Kurti measurements
                        kurtiLength = kurtiFields.find { it.fieldId == "kurtiLength" }?.value ?: "",
                        fullShoulder = kurtiFields.find { it.fieldId == "fullShoulder" }?.value ?: "",
                        upperChestRound = kurtiFields.find { it.fieldId == "upperChestRound" }?.value ?: "",
                        chestRound = kurtiFields.find { it.fieldId == "chestRound" }?.value ?: "",
                        waistRound = kurtiFields.find { it.fieldId == "waistRound" }?.value ?: "",
                        shoulderToApex = kurtiFields.find { it.fieldId == "shoulderToApex" }?.value ?: "",
                        apexToApex = kurtiFields.find { it.fieldId == "apexToApex" }?.value ?: "",
                        shoulderToLowChestLength = kurtiFields.find { it.fieldId == "shoulderToLowChestLength" }?.value ?: "",
                        skapLength = kurtiFields.find { it.fieldId == "skapLength" }?.value ?: "",
                        skapLengthRound = kurtiFields.find { it.fieldId == "skapLengthRound" }?.value ?: "",
                        hipRound = kurtiFields.find { it.fieldId == "hipRound" }?.value ?: "",
                        frontNeckDeep = kurtiFields.find { it.fieldId == "frontNeckDeep" }?.value ?: "",
                        frontNeckWidth = kurtiFields.find { it.fieldId == "frontNeckWidth" }?.value ?: "",
                        backNeckDeep = kurtiFields.find { it.fieldId == "backNeckDeep" }?.value ?: "",
                        readyShoulder = kurtiFields.find { it.fieldId == "readyShoulder" }?.value ?: "",
                        sleevesHeightShort = kurtiFields.find { it.fieldId == "sleevesHeightShort" }?.value ?: "",
                        sleevesHeightElbow = kurtiFields.find { it.fieldId == "sleevesHeightElbow" }?.value ?: "",
                        sleevesHeightThreeQuarter = kurtiFields.find { it.fieldId == "sleevesHeightThreeQuarter" }?.value ?: "",
                        sleevesRound = kurtiFields.find { it.fieldId == "sleevesRound" }?.value ?: "",
                        // Pant measurements
                        pantWaist = pantFields.find { it.fieldId == "pantWaist" }?.value ?: "",
                        pantLength = pantFields.find { it.fieldId == "pantLength" }?.value ?: "",
                        pantHip = pantFields.find { it.fieldId == "pantHip" }?.value ?: "",
                        pantBottom = pantFields.find { it.fieldId == "pantBottom" }?.value ?: "",
                        // Blouse measurement
                        blouseLength = blouseFields.find { it.fieldId == "blouseLength" }?.value ?: "",
                        lastUpdated = System.currentTimeMillis()
                    )
                    
                    database.measurementDao().insertMeasurement(measurement)
                    
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Measurements saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Error saving measurements: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
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
        fun newInstance(customer: Customer, selectedTab: String = "KURTI"): EditMeasurementsFragment {
            val fragment = EditMeasurementsFragment()
            val bundle = Bundle().apply {
                putInt("customerId", customer.id)
                putString("firstName", customer.firstName)
                putString("lastName", customer.lastName)
                putString("address", customer.address)
                putString("mobile", customer.mobile)
                putString("alternateMobile", customer.alternateMobile)
                putString("birthDate", customer.birthDate)
                putString("selectedTab", selectedTab)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

