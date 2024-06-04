package com.capston2024.capstonapp.presentation.main.bag

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.DialogOrdercheckBinding
import com.capston2024.capstonapp.extension.OrderState
import com.capston2024.capstonapp.presentation.main.MainViewModel
import com.capston2024.capstonapp.presentation.order.OrderFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderCheckDialog(
    private val bagAdapter: BagAdapter,
    private val mainViewModel: MainViewModel,
) : DialogFragment()  {
    private var _binding: DialogOrdercheckBinding? = null
    private val binding
        get() = _binding!!
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.CustomDialog)

        dialog.setContentView(R.layout.dialog_ordercheck)
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
        _binding = DialogOrdercheckBinding.inflate(inflater, container, false)
        val view = binding.root
        Log.d("ordercheckdialog","orderstate: ${mainViewModel.orderState.value}")
        collectOrderState()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        clickButton()
        return view
    }

    private fun collectOrderState(){
        lifecycleScope.launch {
            //주문 제대로 보냈는지 확인하고 dialog랑 장바구니 화면 삭제
            mainViewModel.orderState.collect { orderState ->
                when (orderState) {
                    is OrderState.Success -> {
                        Log.d("orderckeckdialog","orderstate Success!!")
                        removeBagShowOrder()
                    }

                    is OrderState.Error -> {
                        // 오류 처리
                    }

                    is OrderState.Loading -> {
                        // 로딩 처리
                    }
                }
            }
        }
    }

    private fun removeBagShowOrder() {
        // BagFragment 제거
        //dismiss()
        //장바구니 내역 초기화
        mainViewModel.clearBagList()
        val fragmentManager = requireActivity().supportFragmentManager
        var fragment = fragmentManager.findFragmentById(R.id.fcv_bag)
        fragment?.let {
            mainViewModel.setBagShow(false)

            val transaction = fragmentManager.beginTransaction()
            transaction.remove(it)
            transaction.commit()

            fragmentManager.executePendingTransactions()

            val isFragmentRemoved = fragmentManager.findFragmentById(R.id.fcv_bag) == null
            Log.d("ordercheckdialog", "Fragment removed: $isFragmentRemoved")
        }
        mainViewModel.setOrderStateLoading()
        //orderFragment 보이기
        //fragment=fragmentManager.findFragmentById(R.id.fcv_order)
        fragmentManager.beginTransaction()
            .replace(R.id.fcv_order, OrderFragment())
            .commit()
        mainViewModel.isVisibleOrderList(true)
        dismiss()
    }

    private fun clickButton(){
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOrder.setOnClickListener {
            //장바구니 내역들 주문내역 서버로 보내기
            mainViewModel.setPaymentId("process")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("OrderCheckDialogFragment", "onDestroyView 호출됨")
    }
}