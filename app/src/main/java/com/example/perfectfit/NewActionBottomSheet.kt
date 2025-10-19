package com.example.perfectfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.perfectfit.databinding.BottomSheetNewActionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewActionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetNewActionBinding? = null
    private val binding get() = _binding!!

    private var listener: NewActionListener? = null

    interface NewActionListener {
        fun onNewCustomer()
        fun onNewOrder()
    }

    fun setListener(listener: NewActionListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetNewActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewCustomer.setOnClickListener {
            listener?.onNewCustomer()
            dismiss()
        }

        binding.btnNewOrder.setOnClickListener {
            listener?.onNewOrder()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NewActionBottomSheet"
        
        fun newInstance(): NewActionBottomSheet {
            return NewActionBottomSheet()
        }
    }
}

