package com.capston2024.capstonapp.presentation.order

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
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
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
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

        val circle1: View = payingDL.findViewById(R.id.circle1)
        val circle2: View = payingDL.findViewById(R.id.circle2)
        val circle3: View = payingDL.findViewById(R.id.circle3)
        animateCircle(circle1)
        animateCircle(circle2, delay = 200)
        animateCircle(circle3, delay = 400)

        Handler(Looper.getMainLooper()).postDelayed({
            payingDL.dismiss()
            paymentListener?.completePayment()
        }, 5000)
    }

    private fun animateProgressBar(progressBar: ProgressBar) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 4700
        animator.start()
    }

    private fun animateCircle(view: View, delay: Long = 0) {

        val animatorSet = AnimatorSet()

        val translateYAnimatorUp = ObjectAnimator.ofFloat(view, "translationY", -5f, 5f)
        translateYAnimatorUp.duration = 300
        translateYAnimatorUp.interpolator = AccelerateDecelerateInterpolator()

        val translateYAnimatorDown = ObjectAnimator.ofFloat(view, "translationY", 5f, -5f)
        translateYAnimatorDown.duration = 300
        translateYAnimatorDown.interpolator = DecelerateInterpolator()

        animatorSet.playSequentially(translateYAnimatorUp, translateYAnimatorDown)
        animatorSet.startDelay = delay
        animatorSet.start()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animatorSet)
                animatorSet.startDelay = 0
                animatorSet.start()

            }
        })

    }
}

