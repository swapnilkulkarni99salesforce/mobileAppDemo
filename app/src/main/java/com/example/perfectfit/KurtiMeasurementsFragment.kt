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
    private var adapter: MeasurementListAdapter? = null

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
        recyclerView.setHasFixedSize(false)
        recyclerView.isNestedScrollingEnabled = false
        
        observeMeasurements()
    }
    
    private fun observeMeasurements() {
        // Get the parent fragment and observe its measurement LiveData (only once)
        (parentFragment as? ClientFitProfileFragment)?.let { parent ->
            parent.getMeasurementLiveData()?.observe(viewLifecycleOwner) { measurement ->
                updateUI(measurement)
            }
        }
    }
    
    private fun updateUI(measurement: com.example.perfectfit.models.Measurement?) {
        val measurements = mutableListOf<MeasurementItem>()
        
        if (measurement != null) {
            measurements.add(MeasurementItem("Kurti Length", measurement.kurtiLength))
            measurements.add(MeasurementItem("Full Shoulder", measurement.fullShoulder))
            measurements.add(MeasurementItem("Upper Chest Round", measurement.upperChestRound))
            measurements.add(MeasurementItem("Chest Round", measurement.chestRound))
            measurements.add(MeasurementItem("Waist Round", measurement.waistRound))
            measurements.add(MeasurementItem("Shoulder to Apex", measurement.shoulderToApex))
            measurements.add(MeasurementItem("Apex to Apex", measurement.apexToApex))
            measurements.add(MeasurementItem("Shoulder to Low Chest Length", measurement.shoulderToLowChestLength))
            measurements.add(MeasurementItem("Skap Length", measurement.skapLength))
            measurements.add(MeasurementItem("Skap Length Round", measurement.skapLengthRound))
            measurements.add(MeasurementItem("Hip Round", measurement.hipRound))
            measurements.add(MeasurementItem("Front Neck Deep", measurement.frontNeckDeep))
            measurements.add(MeasurementItem("Front Neck Width", measurement.frontNeckWidth))
            measurements.add(MeasurementItem("Back Neck Deep", measurement.backNeckDeep))
            measurements.add(MeasurementItem("Ready Shoulder", measurement.readyShoulder))
            measurements.add(MeasurementItem("Sleeves Height (Short)", measurement.sleevesHeightShort))
            measurements.add(MeasurementItem("Sleeves Height (Elbow)", measurement.sleevesHeightElbow))
            measurements.add(MeasurementItem("Sleeves Height (3/4th)", measurement.sleevesHeightThreeQuarter))
            measurements.add(MeasurementItem("Sleeves Round", measurement.sleevesRound))
        } else {
            measurements.add(MeasurementItem("Kurti Length", ""))
            measurements.add(MeasurementItem("Full Shoulder", ""))
            measurements.add(MeasurementItem("Upper Chest Round", ""))
            measurements.add(MeasurementItem("Chest Round", ""))
            measurements.add(MeasurementItem("Waist Round", ""))
            measurements.add(MeasurementItem("Shoulder to Apex", ""))
            measurements.add(MeasurementItem("Apex to Apex", ""))
            measurements.add(MeasurementItem("Shoulder to Low Chest Length", ""))
            measurements.add(MeasurementItem("Skap Length", ""))
            measurements.add(MeasurementItem("Skap Length Round", ""))
            measurements.add(MeasurementItem("Hip Round", ""))
            measurements.add(MeasurementItem("Front Neck Deep", ""))
            measurements.add(MeasurementItem("Front Neck Width", ""))
            measurements.add(MeasurementItem("Back Neck Deep", ""))
            measurements.add(MeasurementItem("Ready Shoulder", ""))
            measurements.add(MeasurementItem("Sleeves Height (Short)", ""))
            measurements.add(MeasurementItem("Sleeves Height (Elbow)", ""))
            measurements.add(MeasurementItem("Sleeves Height (3/4th)", ""))
            measurements.add(MeasurementItem("Sleeves Round", ""))
        }
        
        // Log to verify all measurements are added
        android.util.Log.d("KurtiFragment", "Total measurements: ${measurements.size}")
        
        // Create new adapter with all 19 measurements
        adapter = MeasurementListAdapter(measurements)
        recyclerView.adapter = adapter
        adapter?.notifyDataSetChanged()
    }
}

