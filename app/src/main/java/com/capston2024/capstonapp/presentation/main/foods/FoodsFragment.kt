package com.capston2024.capstonapp.presentation.main.foods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.FragmentMode
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.databinding.FragmentFoodsBinding
import com.capston2024.capstonapp.extension.FoodState
import com.capston2024.capstonapp.presentation.main.MainActivity
import com.capston2024.capstonapp.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable

@AndroidEntryPoint
class FoodsFragment(private var id:Int) : Fragment() {
    private var _binding: FragmentFoodsBinding? = null
    private val binding: FragmentFoodsBinding
        get() = requireNotNull(_binding) { "null" }

    private lateinit var mainViewModel: MainViewModel
    private val foodsViewModel: FoodsViewModel by viewModels()
    private lateinit var foodsAdapter: FoodsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodsBinding.inflate(inflater, container, false)
        mainViewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodsAdapter = FoodsAdapter()
        binding.rvFoods.adapter = foodsAdapter
        showPhotos()
        setClickListener()
        observeShowingBag()
    }

    fun showPhotos() {
        foodsViewModel.getData(id)
        mainViewModel.menuID.observe(viewLifecycleOwner){ menuId ->
            foodsViewModel.getData(menuId)
        }

        lifecycleScope.launch {
            foodsViewModel.foodState.collect { foodState ->
                when (foodState) {
                    is FoodState.Success -> {
                        getFoodListWithPicture()
                    }

                    is FoodState.Error -> {
                    }

                    is FoodState.Loading -> {
                    }
                }
            }
        }
    }

    private fun getFoodListWithPicture(){
        lifecycleScope.launch {
            foodsViewModel.foodState.collect{
                when(it){
                    is FoodState.Success -> {
                        foodsAdapter.submitList(it.foodList)
                    }
                    is FoodState.Error -> {
                    }
                    is FoodState.Loading-> {

                    }
                }
            }
        }
    }

    private fun setClickListener() {
        // 아이템 클릭 리스너 설정
        foodsAdapter.setOnItemClickListener(object : FoodsViewHolder.OnItemClickListener {
            override fun onFoodItemClick(foodItem: ResponseFoodDto.Food) {
                val activity = requireActivity() as MainActivity
                val bagFragment=activity.bagFragment
                val bag = Bag(foodItem.id, foodItem.name, foodItem.price, 1) // Bag 객체 생성
                val bundle = Bundle().apply {
                    putSerializable("selectedFood", bag as Serializable) // Bag 객체를 직렬화하여 Bundle에 추가
                }
                bagFragment.arguments=bundle

                if(!mainViewModel.isBagShow.value!!){
                    activity.showBagFragment(bagFragment, FragmentMode.AI_MODE)
                }else{
                    bagFragment.setBag()
                }
                activity.supportFragmentManager.executePendingTransactions()
            }
        })
    }

    private fun observeShowingBag(){
        mainViewModel.isBagShow.observe(viewLifecycleOwner, Observer {isBagShow ->
            if(isBagShow){
                changeSpanCount(2)
            }else
                changeSpanCount(3)
        })
    }

    fun changeSpanCount(newSpanCount: Int) {
        val layoutManager = binding.rvFoods.layoutManager as? GridLayoutManager
        layoutManager?.spanCount = newSpanCount
        binding.rvFoods.layoutManager = layoutManager
        binding.rvFoods.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}