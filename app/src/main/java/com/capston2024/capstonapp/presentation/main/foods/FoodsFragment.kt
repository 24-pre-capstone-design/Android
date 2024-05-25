package com.capston2024.capstonapp.presentation.main.foods

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aallam.openai.api.core.Parameters
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.FragmentType
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.databinding.FragmentFoodsBinding
import com.capston2024.capstonapp.extension.FoodState
import com.capston2024.capstonapp.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import java.io.Serializable

@AndroidEntryPoint
class FoodsFragment(private val id:Int) : Fragment() {
    private var _binding: FragmentFoodsBinding? = null
    private val binding: FragmentFoodsBinding
        get() = requireNotNull(_binding) { "null" }

    private val courseViewModel: FoodsViewModel by viewModels()
    private lateinit var foodsAdapter: FoodsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodsAdapter = FoodsAdapter()
        binding.rvFoods.adapter = foodsAdapter
        showPhotos()
        setClickListener()
    }

    fun showPhotos() {
        courseViewModel.getData(id)
        lifecycleScope.launch {
            courseViewModel.foodState.collect { foodState ->
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
            courseViewModel.foodState.collect{
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
                val bag = Bag(foodItem.name, foodItem.price, 1) // Bag 객체 생성
                val bundle = Bundle().apply {
                    putSerializable("selectedFood", bag as Serializable) // Bag 객체를 직렬화하여 Bundle에 추가
                }
                bagFragment.arguments=bundle

                if (!activity.bagIsShow)
                    activity.showFragments(R.id.fcv_bag, bagFragment, "bagFragment", FragmentType.AI_MODE)
                else {
                    bagFragment.setBag()
                }
                activity.supportFragmentManager.executePendingTransactions()
            }
        })
    }

    fun changeSpanCount(newSpanCount: Int) {
        val layoutManager = binding.rvFoods.layoutManager as? GridLayoutManager
        layoutManager?.spanCount = newSpanCount
        binding.rvFoods.layoutManager = layoutManager
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{// functioncall용 전처리
        fun name(): String{
            return "FoodOrderFunction"
        }
        fun description(): String {
            return "사용자의 주문을 받아 장바구니에 추가하는 함수입니다. "
        }
        fun params(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("item") {
                        put("type", "string")
                        put("description", "현재 주문할 음식의 이름 입니다.")
                    }
                    putJsonObject("quantity"){
                        put("type","string")
                        put("description","추가할 아이템의 양 입니다.")
                    }
                }
                putJsonArray("required") {
                    add("item")
                    add("quantity")
                }
            }
            return params
        }
        suspend fun foodOrderFunction(item: String, quantity: String):String{


            return ""
        }
    }


}