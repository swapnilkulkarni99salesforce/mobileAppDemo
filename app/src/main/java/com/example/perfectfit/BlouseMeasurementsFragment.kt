package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.adapters.MeasurementListAdapter
import com.example.perfectfit.models.MeasurementItem

class BlouseMeasurementsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: MeasurementListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_blouse_measurements, container, false)
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
        (parentFragment as? ClientFitProfileFragment)?.let { parent ->
            parent.getMeasurementLiveData()?.observe(viewLifecycleOwner) { measurement ->
                updateUI(measurement)
            }
        }
    }
    
    private fun updateUI(measurement: com.example.perfectfit.models.Measurement?) {
        val measurements = mutableListOf<MeasurementItem>()
        
        if (measurement != null) {
            measurements.add(MeasurementItem("Blouse Length", measurement.blouseLength))
            measurements.add(MeasurementItem("Full Shoulder", measurement.blouseFullShoulder))
            measurements.add(MeasurementItem("Chest", measurement.blouseChest))
            measurements.add(MeasurementItem("Waist", measurement.blouseWaist))
            measurements.add(MeasurementItem("Shoulder to Apex", measurement.blouseShoulderToApex))
            measurements.add(MeasurementItem("Apex to Apex", measurement.blouseApexToApex))
            measurements.add(MeasurementItem("Back Length", measurement.blouseBackLength))
            measurements.add(MeasurementItem("Front Neck Deep", measurement.blouseFrontNeckDeep))
            measurements.add(MeasurementItem("Front Neck Width", measurement.blouseFrontNeckWidth))
            measurements.add(MeasurementItem("Back Neck Deep", measurement.blouseBackNeckDeep))
            measurements.add(MeasurementItem("Ready Shoulder", measurement.blouseReadyShoulder))
            measurements.add(MeasurementItem("Sleeves Height (Short)", measurement.blouseSleevesHeightShort))
            measurements.add(MeasurementItem("Sleeves Height (Elbow)", measurement.blouseSleevesHeightElbow))
            measurements.add(MeasurementItem("Sleeves Height (3/4th)", measurement.blouseSleevesHeightThreeQuarter))
            measurements.add(MeasurementItem("Sleeves Round", measurement.blouseSleevesRound))
            measurements.add(MeasurementItem("Hook On", measurement.blouseHookOn))
        } else {
            measurements.add(MeasurementItem("Blouse Length", ""))
            measurements.add(MeasurementItem("Full Shoulder", ""))
            measurements.add(MeasurementItem("Chest", ""))
            measurements.add(MeasurementItem("Waist", ""))
            measurements.add(MeasurementItem("Shoulder to Apex", ""))
            measurements.add(MeasurementItem("Apex to Apex", ""))
            measurements.add(MeasurementItem("Back Length", ""))
            measurements.add(MeasurementItem("Front Neck Deep", ""))
            measurements.add(MeasurementItem("Front Neck Width", ""))
            measurements.add(MeasurementItem("Back Neck Deep", ""))
            measurements.add(MeasurementItem("Ready Shoulder", ""))
            measurements.add(MeasurementItem("Sleeves Height (Short)", ""))
            measurements.add(MeasurementItem("Sleeves Height (Elbow)", ""))
            measurements.add(MeasurementItem("Sleeves Height (3/4th)", ""))
            measurements.add(MeasurementItem("Sleeves Round", ""))
            measurements.add(MeasurementItem("Hook On", ""))
        }
        
        android.util.Log.d("BlouseFragment", "Total measurements: ${measurements.size}")
        
        adapter = MeasurementListAdapter(measurements)
        recyclerView.adapter = adapter
        adapter?.notifyDataSetChanged()
    }
}

