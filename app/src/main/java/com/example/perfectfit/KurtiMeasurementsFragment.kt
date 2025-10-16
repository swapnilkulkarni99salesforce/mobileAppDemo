package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.adapters.MeasurementListAdapter
import com.example.perfectfit.models.MeasurementItem

class KurtiMeasurementsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_kurti_measurements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.measurements_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        observeMeasurements()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when returning from edit screen
        observeMeasurements()
    }
    
    private fun observeMeasurements() {
        // Get the parent fragment and observe its measurement LiveData
        (parentFragment as? ClientFitProfileFragment)?.let { parent ->
            parent.getMeasurementLiveData()?.observe(viewLifecycleOwner) { measurement ->
                updateUI(measurement)
            }
        }
    }
    
    private fun updateUI(measurement: com.example.perfectfit.models.Measurement?) {
        val measurements = if (measurement != null) {
            listOf(
                MeasurementItem("Kurti Length", measurement.kurtiLength),
                MeasurementItem("Full Shoulder", measurement.fullShoulder),
                MeasurementItem("Upper Chest Round", measurement.upperChestRound),
                MeasurementItem("Chest Round", measurement.chestRound),
                MeasurementItem("Waist Round", measurement.waistRound),
                MeasurementItem("Shoulder to Apex", measurement.shoulderToApex),
                MeasurementItem("Apex to Apex", measurement.apexToApex),
                MeasurementItem("Shoulder to Low Chest Length", measurement.shoulderToLowChestLength),
                MeasurementItem("Skap Length", measurement.skapLength),
                MeasurementItem("Skap Length Round", measurement.skapLengthRound),
                MeasurementItem("Hip Round", measurement.hipRound),
                MeasurementItem("Front Neck Deep", measurement.frontNeckDeep),
                MeasurementItem("Front Neck Width", measurement.frontNeckWidth),
                MeasurementItem("Back Neck Deep", measurement.backNeckDeep),
                MeasurementItem("Ready Shoulder", measurement.readyShoulder),
                MeasurementItem("Sleeves Height (Short)", measurement.sleevesHeightShort),
                MeasurementItem("Sleeves Height (Elbow)", measurement.sleevesHeightElbow),
                MeasurementItem("Sleeves Height (3/4th)", measurement.sleevesHeightThreeQuarter),
                MeasurementItem("Sleeves Round", measurement.sleevesRound)
            )
        } else {
            listOf(
                MeasurementItem("Kurti Length", ""),
                MeasurementItem("Full Shoulder", ""),
                MeasurementItem("Upper Chest Round", ""),
                MeasurementItem("Chest Round", ""),
                MeasurementItem("Waist Round", ""),
                MeasurementItem("Shoulder to Apex", ""),
                MeasurementItem("Apex to Apex", ""),
                MeasurementItem("Shoulder to Low Chest Length", ""),
                MeasurementItem("Skap Length", ""),
                MeasurementItem("Skap Length Round", ""),
                MeasurementItem("Hip Round", ""),
                MeasurementItem("Front Neck Deep", ""),
                MeasurementItem("Front Neck Width", ""),
                MeasurementItem("Back Neck Deep", ""),
                MeasurementItem("Ready Shoulder", ""),
                MeasurementItem("Sleeves Height (Short)", ""),
                MeasurementItem("Sleeves Height (Elbow)", ""),
                MeasurementItem("Sleeves Height (3/4th)", ""),
                MeasurementItem("Sleeves Round", "")
            )
        }
        
        recyclerView.adapter = MeasurementListAdapter(measurements)
    }
}

