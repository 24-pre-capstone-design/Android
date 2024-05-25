package com.capston2024.capstonapp.presentation.main.bag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.databinding.FragmentBagBinding
import com.capston2024.capstonapp.presentation.main.MainActivity
import com.capston2024.capstonapp.presentation.main.MainViewModel

class BagFragment : Fragment() {
    private var _binding: FragmentBagBinding? = null
    private val binding:FragmentBagBinding
        get() = requireNotNull(_binding){ "바인딩 객체 생성 안됨" }
    private lateinit var bagAdapter: BagAdapter
    private lateinit var mainViewModel: MainViewModel

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
        clickButtons()
    }

    private fun setViewModelAndAdapter(){
        mainViewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        bagAdapter= BagAdapter(requireContext())
        binding.rvBag.adapter=bagAdapter
        val activity=requireActivity() as MainActivity
        activity.bagIsShow=true
    }

    fun setBag(){
        val foodItem = arguments?.getSerializable("selectedFood") as Bag
        Log.d("fooditem", "Item name: ${foodItem.name}")
        bagAdapter.addBagList(foodItem)
        binding.rvBag.scrollToPosition(bagAdapter.getBagList().size-1)

        setCount()
    }

    fun setCount(){
        var numberOfFoods=bagAdapter.itemCount
        var count=bagAdapter.getTotalCount()
        binding.tvCount.text=getString(R.string.bag_total_count,numberOfFoods,count)
        binding.tvTotalPrice.text=getString(R.string.bag_price,bagAdapter.getTotalPrice())
    }

    private fun clickButtons(){
        binding.btnOrder.setOnClickListener {
            //Log.d("bagfragment", "bagfragment - btnOrder clicked!!!!")
            val dialog= OrderCheckDialog(bagAdapter, mainViewModel)
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