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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_blouse_measurements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val measurement = (parentFragment as? ClientFitProfileFragment)?.getMeasurement()
        val recyclerView = view.findViewById<RecyclerView>(R.id.blouse_measurements_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        val measurements = if (measurement != null) {
            listOf(
                MeasurementItem("Blouse Length", measurement.blouseLength)
            )
        } else {
            listOf(
                MeasurementItem("Blouse Length", "")
            )
        }
        
        recyclerView.adapter = MeasurementListAdapter(measurements)
    }
}

