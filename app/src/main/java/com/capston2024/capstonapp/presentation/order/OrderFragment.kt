package com.capston2024.capstonapp.presentation.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.FragmentOrderBinding
import com.capston2024.capstonapp.presentation.main.MainViewModel

class OrderFragment:Fragment() {
    private var _binding:FragmentOrderBinding?=null
    private val binding:FragmentOrderBinding
        get()= requireNotNull(_binding){"null"}

    private lateinit var orderAdapter: OrderAdapter
    private lateinit var viewModel:MainViewModel


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
    }

    private fun setViewModelAndAdapter(){
        viewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        orderAdapter= OrderAdapter(requireContext(), viewModel)
        binding.rvOrder.adapter=orderAdapter
        binding.tvTotalPrice.text=getString(R.string.bag_price, orderAdapter.getTotalPrice())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}