package com.capston2024.capstonapp.presentation.main.bag

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.Printer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.DialogOrdercheckBinding
import com.capston2024.capstonapp.presentation.main.MainActivity
import com.capston2024.capstonapp.presentation.main.MainViewModel
import com.capston2024.capstonapp.presentation.order.OrderFragment

class OrderCheckDialog(
    //private val bagItems:List<ResponseMockDto.MockModel>
    private val bagAdapter: BagAdapter,
    private val mainViewModel: MainViewModel
): DialogFragment() {
    private var _binding: DialogOrdercheckBinding?=null
    private val binding
        get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog=Dialog(requireContext(),R.style.CustomDialog)

        dialog.setContentView(R.layout.dialog_ordercheck)
        val width=resources.getDimensionPixelSize(R.dimen.dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.dialog_height)
        dialog.window?.setLayout(width, height)

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=DialogOrdercheckBinding.inflate(inflater,container,false)
        val view=binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOrder.setOnClickListener {
            processOrder()
            dismiss()
        }
        return view
    }

    // Order 버튼 클릭 시 처리
    private fun processOrder() {
        bagAdapter.addOrderList()

        // BagFragment 제거
        //dismiss()
        val fragmentManager=requireActivity().supportFragmentManager
        var fragment=fragmentManager.findFragmentById(R.id.fcv_bag)
        fragment?.let {
            val activity=requireActivity() as MainActivity
            activity.bagIsShow=false

            val transaction = fragmentManager.beginTransaction()
            transaction.remove(it)
            transaction.commit()

            fragmentManager.executePendingTransactions()

            val isFragmentRemoved = fragmentManager.findFragmentById(R.id.fcv_bag) == null
            Log.d("Fragment", "Fragment removed: $isFragmentRemoved")
        }
        //orderFragment 보이기
        //fragment=fragmentManager.findFragmentById(R.id.fcv_order)
        fragmentManager.beginTransaction()
            .replace(R.id.fcv_order,OrderFragment())
            .commit()
        mainViewModel.isVisibleOrderList(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}