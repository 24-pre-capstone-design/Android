package com.capston2024.capstonapp.presentation.main.bag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.databinding.FragmentBagBinding
import com.capston2024.capstonapp.presentation.main.MainViewModel
import java.text.NumberFormat
import java.util.Locale

class BagFragment : Fragment(), BagListUpdateListener {
    private var _binding: FragmentBagBinding? = null
    private val binding: FragmentBagBinding
        get() = requireNotNull(_binding) { "바인딩 객체 생성 안됨" }
    private lateinit var bagAdapter: BagAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModelAndAdapter()
        setBag()
        clickButtons()
    }

    private fun setViewModelAndAdapter() {
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        bagAdapter = BagAdapter(requireContext(), this)
        binding.rvBag.adapter = bagAdapter
        mainViewModel.setBagShow(true)
    }

    fun setBag() {
        var foodItem = arguments?.getSerializable("selectedFood") as Bag
        bagAdapter.addBagList(foodItem)
        binding.rvBag.scrollToPosition(bagAdapter.getBagList().size - 1)

        setCount()

        mainViewModel.bagList.observe(viewLifecycleOwner) { bagList ->
            bagAdapter.updateList(bagList)
        }
    }

    fun setCount() {
        var numberOfFoods = bagAdapter.itemCount
        var count = bagAdapter.getTotalCount()
        val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
        binding.tvCount.text = getString(R.string.bag_total_count, numberOfFoods, count)
        binding.tvTotalPrice.text =
            getString(R.string.bag_price, formatter.format(bagAdapter.getTotalPrice()))
    }

    private fun clickButtons() {
        binding.btnOrder.setOnClickListener {
            val dialog = OrderCheckDialog(mainViewModel)
            dialog.isCancelable = false
            activity?.let { dialog.show(it.supportFragmentManager, "ConfirmDialog") }
        }
    }

    fun deleteBagFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val bagFragment = fragmentManager.findFragmentById(R.id.fcv_bag)
        bagFragment?.let {
            mainViewModel.setBagShow(false)

            val transaction = fragmentManager.beginTransaction()
            transaction.remove(it)
            transaction.commit()

            fragmentManager.executePendingTransactions()
        }
    }

    override fun onBagListUpdated(updatedList: MutableList<Bag>) {
        mainViewModel.updateBagList(updatedList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}