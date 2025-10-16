package com.example.perfectfit.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.perfectfit.R
import com.example.perfectfit.models.EditableMeasurementField
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditableMeasurementAdapter(
    private val fields: List<EditableMeasurementField>
) : RecyclerView.Adapter<EditableMeasurementAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val inputLayout: TextInputLayout = view.findViewById(R.id.measurement_input_layout)
        private val input: TextInputEditText = view.findViewById(R.id.measurement_input)
        
        fun bind(field: EditableMeasurementField) {
            // Set the hint to show the label
            inputLayout.hint = field.label
            
            // Set current value
            input.setText(field.value)
            
            // Remove previous text watchers to avoid conflicts
            input.tag?.let { tag ->
                if (tag is TextWatcher) {
                    input.removeTextChangedListener(tag)
                }
            }
            
            // Add text watcher to update the field value
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    field.value = s.toString()
                }
            }
            
            input.addTextChangedListener(textWatcher)
            input.tag = textWatcher
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_measurement_editable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fields[position])
    }

    override fun getItemCount(): Int = fields.size
}

