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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.databinding.DialogOrdercheckBinding
import com.capston2024.capstonapp.extension.OrderState
import com.capston2024.capstonapp.extension.PaymentIdState
import com.capston2024.capstonapp.presentation.main.MainActivity
import com.capston2024.capstonapp.presentation.main.MainViewModel
import com.capston2024.capstonapp.presentation.order.OrderFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderCheckDialog(
    private val bagAdapter: BagAdapter,
    private val mainViewModel: MainViewModel,
) : DialogFragment(), OrderCheckDialogCallback {
    private var _binding: DialogOrdercheckBinding? = null
    private val binding
        get() = _binding!!

    private val bagViewModel: BagViewModel by viewModels()

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
        collectOrderState()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOrder.setOnClickListener {
            processOrder()
        }
        return view
    }

    private fun collectOrderState(){
        lifecycleScope.launch {
            bagViewModel.orderState.collect { orderState ->
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

    // Order 버튼 클릭 시 처리
    private fun processOrder() {
        mainViewModel.setOrderCheckDialogCallback(this)
        mainViewModel.setPaymentId("process")

        //handleOrderDetails(mainViewModel.getPaymentId()!!)


        //mainViewModel.processOrder()
        /*if (mainViewModel.getHasPaymentIdBeenset()) {
            handleOrderDetails(mainViewModel.getPaymentId())
            return
        }*/


        /*mainViewModel.setPaymentId()
        lifecycleScope.launch {
            mainViewModel.paymentIdState.collect { state ->
                when (state) {
                    is PaymentIdState.Loading -> {
                        Log.d("ordercheckdialog", "paymentid state is loading")
                        // Loading 상태 처리
                    }

                    is PaymentIdState.Success -> {
                        Log.d("ordercheckdialog", "paymentidstate is success!!")
                        // Success 상태 처리
                        handleOrderDetails(state.paymentId)
                        // paymentId 사용
                    }

                    is PaymentIdState.Error -> {
                        Log.e("ordercheckdialog", "paymentidstate is error")

                    }
                }
                // 다른 상태 처리
            }
        }*/
    }
    override fun handleOrderDetails(paymentId: Int) {
        Log.d("ordeercheckdialog","handleorderdetail is run")
        //paymentid 설정됨 // paymentId 가져오기
        if (paymentId != null) {
            // 장바구니 내역 가져오기
            bagViewModel.makeBagList(bagAdapter.getBagList())
            Log.d("ordercheckdialog", "paymentid: $paymentId")
            bagAdapter.initializeBagList()
            // 주문내역으로 보내기
            bagViewModel.makeOrderHistory(paymentId)
        }
        Log.d("ordercheckdialog","handleorderdetail is end")
    }

    private fun removeBagShowOrder() {
        // BagFragment 제거
        //dismiss()
        val fragmentManager = requireActivity().supportFragmentManager
        var fragment = fragmentManager.findFragmentById(R.id.fcv_bag)
        fragment?.let {
            mainViewModel.setBagShow(false)

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
            .replace(R.id.fcv_order, OrderFragment())
            .commit()
        mainViewModel.isVisibleOrderList(true)
        dismiss()
    }

    /*override fun onDetach() {
        super.onDetach()
        Log.d("ordercheckdialog", "Fragment detached")
    }
    override fun onPause() {
        super.onPause()
        Log.d("OrderCheckDialogFragment", "onPause 호출됨")
    }

    override fun onStop() {
        super.onStop()
        Log.d("OrderCheckDialogFragment", "onStop 호출됨")
    }*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("OrderCheckDialogFragment", "onDestroyView 호출됨")
    }/*
    override fun onDestroy() {
        super.onDestroy()
        Log.d("OrderCheckDialogFragment", "onDestroy 호출됨")
    }*/
}

/*//장바구니 내역 가져오기
   bagViewModel.makeBagList(bagAdapter.getBagList())

   //주문내역으로 보내기
   bagViewModel.makeOrderHistory(paymentId, bagViewModel.getBagList())
   lifecycleScope.launch {
       bagViewModel.orderState.collect{orderState ->
           when(orderState){
               is OrderState.Success->{
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
               is OrderState.Error -> {

               }
               is OrderState.Loading -> {

               }
           }
       }*/


/*//paymentId 가져옴
mainViewModel.setPaymentId()

viewLifecycleOwner.lifecycleScope.launch {
    mainViewModel.paymentId.observe(viewLifecycleOwner) { paymentId ->
        paymentId?.let {
            makeOrderList(paymentId)
        } ?: run {
            Log.e("ordercheckdialog","paymentId is null")
        }
    }
}*/

// val paymentId = mainViewModel.paymentId.value!!

/*private fun makeOrderList(paymentId:Int){
    Log.d("ordercheckdialog","paymentId: ${paymentId}")
    //장바구니 내역 가져오기
    bagViewModel.makeBagList(bagAdapter.getBagList())

    //주문내역으로 보내기
    bagViewModel.makeOrderHistory(paymentId, bagViewModel.getBagList())
    lifecycleScope.launch {
        bagViewModel.orderState.collect{orderState ->
            when(orderState){
                is OrderState.Success->{
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
                is OrderState.Error -> {

                }
                is OrderState.Loading -> {

                }
            }
        }
    }
}*/


