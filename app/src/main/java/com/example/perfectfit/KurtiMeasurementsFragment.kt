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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_kurti_measurements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val measurement = (parentFragment as? ClientFitProfileFragment)?.getMeasurement()
        
        if (measurement != null) {
            val recyclerView = view.findViewById<RecyclerView>(R.id.measurements_recycler)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            
            val measurements = listOf(
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
            
            recyclerView.adapter = MeasurementListAdapter(measurements)
        }
    }
}

