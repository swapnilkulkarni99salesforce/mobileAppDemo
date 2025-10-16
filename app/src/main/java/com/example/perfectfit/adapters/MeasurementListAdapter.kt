package com.example.perfectfit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.R
import com.example.perfectfit.models.MeasurementItem

class MeasurementListAdapter(private val measurements: List<MeasurementItem>) :
    RecyclerView.Adapter<MeasurementListAdapter.MeasurementViewHolder>() {

    inner class MeasurementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.measurement_label)
        val value: TextView = view.findViewById(R.id.measurement_value)

        fun bind(item: MeasurementItem) {
            label.text = item.label
            value.text = if (item.value.isNotEmpty()) "${item.value} cm" else "-- cm"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_measurement, parent, false)
        return MeasurementViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        holder.bind(measurements[position])
    }

    override fun getItemCount(): Int = measurements.size
}

