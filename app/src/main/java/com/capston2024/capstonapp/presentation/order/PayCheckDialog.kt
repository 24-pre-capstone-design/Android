package com.capston2024.capstonapp.presentation.order

import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.DialogPaycheckBinding
import com.capston2024.capstonapp.presentation.startend.CompletePaymentActivity

class PayCheckDialog : DialogFragment() {
    private var _binding: DialogPaycheckBinding? = null
    private val binding
        get() = _binding!!

    private var paymentListener: PaymentListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.CustomDialog)

        dialog.setContentView(R.layout.dialog_paycheck)
        val width = resources.getDimensionPixelSize(R.dimen.dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.dialog_height)
        dialog.window?.setLayout(width, height)

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogPaycheckBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        clickButton()
        return view
    }

    private fun clickButton() {
        if(activity is PaymentListener){
            paymentListener=activity as PaymentListener
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOrder.setOnClickListener {
            showPopup()
            dismiss()
        }
    }

    private fun showPopup() {
        val payingDL = Dialog(requireContext())
        payingDL.setContentView(R.layout.popup_paying)
        payingDL.setCancelable(false)
        payingDL.show()

        val payingPB: ProgressBar = payingDL.findViewById(R.id.payingProgressBar)
        animateProgressBar(payingPB)

        Handler(Looper.getMainLooper()).postDelayed({
            payingDL.dismiss()
            paymentListener?.CompletePayment()
        }, 5000)
    }

    private fun animateProgressBar(progressBar: ProgressBar) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 4700
        animator.start()
    }
}

