package com.capston2024.capstonapp.presentation.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.FragmentBagBinding

class BagFragment : Fragment() {
    private var _binding: FragmentBagBinding? = null
    private val binding:FragmentBagBinding
        get() = requireNotNull(_binding){ "바인딩 객체 생성 안됨" }
    private lateinit var bagAdapter: BagAdapter
    private lateinit var viewModel:MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentBagBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModelAndAdapter()
        setBag()
        clickOrderButton()
    }

    private fun setViewModelAndAdapter(){
        viewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        bagAdapter= BagAdapter(requireContext(), viewModel)
        binding.rvBag.adapter=bagAdapter
        val activity=requireActivity() as MainActivity
        activity.bagIsShow=true
    }

    fun setBag(){
        val foodItem = arguments?.getSerializable("selectedFood") as ResponseMockDto.MockModel
        Log.d("fooditem", "Item name: ${foodItem.lastName}")
        bagAdapter.addBagList(foodItem)

        setCount()
    }

    fun setCount(){
        var numberOfFoods=bagAdapter.itemCount
        var count=bagAdapter.getTotalCount()
        binding.tvCount.text=getString(R.string.bag_total_count,numberOfFoods,count)
        binding.tvTotalPrice.text=getString(R.string.bag_price,bagAdapter.getTotalPrice())
    }

    private fun clickOrderButton(){
        binding.btnOrder.setOnClickListener {
            Log.d("clicked", "btnOrder clicked!!!!")
            val dialog=OrderCheckDialog(bagAdapter)
            dialog.isCancelable=false
            activity?.let { dialog.show(it.supportFragmentManager, "ConfirmDialog") }
        }
    }

    fun deleteBagFragment(){
        val fragmentManager=requireActivity().supportFragmentManager
        val bagFragment=fragmentManager.findFragmentById(R.id.fcv_bag)
        Log.d("bagfragment is null?", "bag fragment is null?: ${bagFragment==null}")
        bagFragment?.let {
            val activity=requireActivity() as MainActivity
            activity.bagIsShow=false

            val transaction = fragmentManager.beginTransaction()
            transaction.remove(it)
            transaction.commit()

            fragmentManager.executePendingTransactions()

            val isFragmentRemoved = fragmentManager.findFragmentById(R.id.fcv_bag) == null
            Log.d("Fragment", "Fragment removed: $isFragmentRemoved")
        }
    }

    override fun onDestroyView() {
        Log.d("destroy", "bagfragment is destroy")
        super.onDestroyView()
        _binding=null
    }
}