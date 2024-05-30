package com.capston2024.capstonapp.presentation.main.foods

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.capston2024.capstonapp.R
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
                        //Toast.makeText(activity, "정보 가져오기 성공", Toast.LENGTH_SHORT).show()
                        //pictureViewModel.getData(foodState.foodList)
                        getFoodListWithPicture()
                    }

                    is FoodState.Error -> {
                        //Toast.makeText(activity, "정보 가져오기 실패", Toast.LENGTH_SHORT).show()
                        Log.e("error","foodstate is error: ${foodState.message}")
                    }

                    is FoodState.Loading -> {
                        //Toast.makeText(activity, "로딩중", Toast.LENGTH_SHORT).show()
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
                        Log.e("error","picturestate is error")
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
                // 아이템을 BagFragment로 전달
                //Toast.makeText(activity, "${foodItem.lastName}", Toast.LENGTH_SHORT).show()
                Log.d("courseclick", "coursefragment course clicked item: ${foodItem.name}")
                val activity = requireActivity() as MainActivity
                val bagFragment=activity.bagFragment
                val bag = Bag(foodItem.id, foodItem.name, foodItem.price, 1) // Bag 객체 생성
                val bundle = Bundle().apply {
                    putSerializable("selectedFood", bag as Serializable) // Bag 객체를 직렬화하여 Bundle에 추가
                }
                bagFragment.arguments=bundle

                /*if (!activity.bagIsShow)
                    activity.showFragments(R.id.fcv_bag, bagFragment, FragmentType.AI_MODE)
                else {
                    bagFragment.setBag()
                }*/
                if(!mainViewModel.isBagShow.value!!){
                    activity.showFragments(R.id.fcv_bag, bagFragment, FragmentMode.AI_MODE)
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
    }

    fun updateData(newId: Int) {
        this.id = newId
        // 새로운 데이터로 뷰를 업데이트하는 로직 추가
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}