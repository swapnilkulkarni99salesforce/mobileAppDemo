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

class PantMeasurementsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: MeasurementListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_pant_measurements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.pant_measurements_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
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
        val measurements = if (measurement != null) {
            listOf(
                MeasurementItem("Waist", measurement.pantWaist),
                MeasurementItem("Length", measurement.pantLength),
                MeasurementItem("Hip", measurement.pantHip),
                MeasurementItem("Bottom", measurement.pantBottom)
            )
        } else {
            listOf(
                MeasurementItem("Waist", ""),
                MeasurementItem("Length", ""),
                MeasurementItem("Hip", ""),
                MeasurementItem("Bottom", "")
            )
        }
        
        adapter = MeasurementListAdapter(measurements)
        recyclerView.adapter = adapter
    }
}

