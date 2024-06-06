package com.capston2024.capstonapp.presentation.order

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.DialogPaycheckBinding

class PayCheckDialog : DialogFragment() {
    private var _binding: DialogPaycheckBinding? = null
    private val binding
        get() = _binding!!

    private var paymentListener: PaymentListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog=Dialog(requireContext(), R.style.CustomDialog)

        dialog.setContentView(R.layout.dialog_paycheck)
        val width=resources.getDimensionPixelSize(R.dimen.dialog_width)
        val height=resources.getDimensionPixelSize(R.dimen.dialog_height)
        dialog.window?.setLayout(width,height)

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=DialogPaycheckBinding.inflate(inflater,container,false)
        val view=binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        clickButton()
        return view
    }

    private fun clickButton(){
        if(activity is PaymentListener){
            paymentListener=activity as PaymentListener
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOrder.setOnClickListener {
            paymentListener?.PayingProgress()
            dismiss()
        }
    }
}