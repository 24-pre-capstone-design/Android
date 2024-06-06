package com.capston2024.capstonapp.presentation.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.FragmentOrderBinding
import com.capston2024.capstonapp.extension.OrderHistoryState
import com.capston2024.capstonapp.extension.OrderState
import com.capston2024.capstonapp.extension.PaymentIdState
import com.capston2024.capstonapp.presentation.main.MainViewModel
import com.capston2024.capstonapp.presentation.startend.CompletePaymentActivity
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class OrderFragment:Fragment() {
    private var _binding:FragmentOrderBinding?=null
    private val binding:FragmentOrderBinding
        get()= requireNotNull(_binding){"null"}

    private lateinit var orderAdapter: OrderAdapter
    private lateinit var mainViewModel:MainViewModel
    private lateinit var orderViewModel: OrderViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentOrderBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModelAndAdapter()
        clickButtons()
        showOrderHistory()
    }

    private fun setViewModelAndAdapter(){
        mainViewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        orderAdapter= OrderAdapter(requireContext())
        orderViewModel = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        binding.rvOrder.adapter=orderAdapter

        //paymentid를 서버에 보내서 주문내역 받아옴
        lifecycleScope.launch {
            mainViewModel.orderState.collect{orderState ->
                when(orderState){
                    is OrderState.Success -> {
                        Log.d("orderfragment","orderstate is success")
                        getOrderList()
                    }
                    is OrderState.Loading -> {
                        Log.d("orderfragment","orderstate is loading")
                    }
                    is OrderState.Error->{}
                }
            }
        }
        val formatter= NumberFormat.getNumberInstance(Locale.KOREA)
        orderViewModel.orderTotalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.tvTotalPrice.text = getString(R.string.bag_price, formatter.format(totalPrice?:0))
        }
    }
    private fun getOrderList(){
        lifecycleScope.launch {

            //paymentid를 서버에 보내서 주문내역 받아옴
            mainViewModel.paymentIdState.collect{paymentIdState ->
                when(paymentIdState){
                    is PaymentIdState.Success ->{
                        Log.d("orderfragment","paymentid state success")
                        orderViewModel.getOrderHistory(paymentIdState.paymentId)
                    }
                    is PaymentIdState.Loading -> {}
                    is PaymentIdState.Error -> {}
                }
            }
        }
    }

    private fun showOrderHistory(){
        lifecycleScope.launch {
            //주문내역 서버에서 받아오기 성공했는지 확인하고 화면에 보여줌
            orderViewModel.orderHistoryState.collect{orderHistoryState ->
                when(orderHistoryState){
                    is OrderHistoryState.Success -> {
                        Log.d("orderfragment", "orderhistorystate is success")
                        orderAdapter.getOrderHistoryList(orderHistoryState.orderHistoryList)
                    }
                    is OrderHistoryState.Error -> {
                        Log.e("orderfragment","foodstate is error")
                    }
                    is OrderHistoryState.Loading -> {

                    }
                }
            }
        }
    }

    private fun clickButtons() {
        binding.btnPay.setOnClickListener {
            Log.d("clicked", "btnPay clicked!!!!")
            val dialog = PayCheckDialog()
            dialog.isCancelable = false

            (activity as? AppCompatActivity)?.supportFragmentManager?.let { fragmentManager ->
                dialog.show(fragmentManager, "ConfirmDialog")
            } ?: run {
                Log.e("OrderFragment", "Activity is null or not an AppCompatActivity")
            }
        }

        binding.btnBack.setOnClickListener {
            mainViewModel.isVisibleOrderList(false)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}