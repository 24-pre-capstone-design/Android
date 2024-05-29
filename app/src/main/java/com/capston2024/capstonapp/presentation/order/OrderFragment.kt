package com.capston2024.capstonapp.presentation.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.FragmentOrderBinding
import com.capston2024.capstonapp.extension.OrderHistoryState
import com.capston2024.capstonapp.extension.OrderState
import com.capston2024.capstonapp.extension.PaymentIdState
import com.capston2024.capstonapp.presentation.main.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
    }

    private fun setViewModelAndAdapter(){
        mainViewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        orderAdapter= OrderAdapter(requireContext())
        orderViewModel = ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        mainViewModel.setPaymentId()
        binding.rvOrder.adapter=orderAdapter

        //paymentid가져오기
        lifecycleScope.launch {
            mainViewModel.paymentIdState.collect{paymentIdState ->
                when(paymentIdState){
                    is PaymentIdState.Success -> {
                        if(mainViewModel.getPaymentId()!=null) {
                            orderViewModel.getOrderHistory(mainViewModel.getPaymentId()!!)
                        }else{
                            Log.d("orderfragment","mainviewmodel getpaymentid is null")
                        }

                    }
                    is PaymentIdState.Loading->{}
                    is PaymentIdState.Error->{}
                }
            }
        }

        //주문내역화면에 주문내역 보여주기
        lifecycleScope.launch {
            orderViewModel.orderHistoryState.collect{orderHistoryState ->
                when(orderHistoryState){
                    is OrderHistoryState.Success -> {
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

        orderViewModel.orderTotalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.tvTotalPrice.text = getString(R.string.bag_price, totalPrice?:0)
        }
    }

    private fun clickButtons(){
        binding.btnPay.setOnClickListener {
            Log.d("clicked", "btnPay clicked!!!!")
            val dialog=PayCheckDialog()
            dialog.isCancelable=false
            activity?.let{dialog.show(it.supportFragmentManager, "ConfirmDialog")}
        }

        binding.btnBack.setOnClickListener{
            mainViewModel.isVisibleOrderList(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}