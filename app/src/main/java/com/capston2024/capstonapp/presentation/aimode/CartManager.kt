package com.capston2024.capstonapp.presentation.aimode

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aallam.openai.api.core.Parameters
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.FragmentType
import com.capston2024.capstonapp.presentation.main.MainActivity
import com.capston2024.capstonapp.presentation.main.MainViewModel
import com.capston2024.capstonapp.presentation.main.foods.FoodsFragment
import kotlinx.coroutines.launch
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import java.io.Serializable
import kotlin.collections.mutableMapOf

class CartManager(private val aiViewModel: AIViewModel, private val mainActivity: MainActivity, private val mainViewModel: MainViewModel) {

    // companion object:Fragment() {


    fun name(): String {
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
                putJsonObject("quantity") {
                    put("type", "string")
                    put("description", "추가할 아이템의 양 입니다.")
                }
            }
            putJsonArray("required") {
                add("item")
                add("quantity")
            }
        }
        return params
    }

    fun addFoodToBag(item: String, quantity: Int) {
        // val aiViewModel: AIViewModel by viewModels()
        val foodItem = aiViewModel.getFoodByName(item)// 음식 이름으로 음식 객체를 가져오는 함수
        if (foodItem != null) {
            mainActivity.lifecycleScope.launch {
                val bag = Bag(foodItem.id, foodItem.name, foodItem.price, quantity)
                val bundle = Bundle().apply {
                    putSerializable("selectedFood", bag as Serializable)
                }
                mainActivity.bagFragment.arguments = bundle

                if (!mainViewModel.isBagShow.value!!) {
                    mainActivity.showFragments(
                        R.id.fcv_bag,
                        mainActivity.bagFragment,
                        FragmentType.AI_MODE
                    )
                } else {
                    mainActivity.bagFragment.setBag()
                }
                mainActivity.supportFragmentManager.executePendingTransactions()
            }
        } else {
            Log.e("error", "Food item not found: $item")
        }
    }

    suspend fun foodOrderFunction(item: String, quantity: String): String {
        if (item != null) {
            addFoodToBag(item, quantity.toInt())
            return "${item}을/를 ${quantity}만큼 장바구니에 넣었습니다!"
        } else {
            return "음식을 장바구니에 추가할 수 없습니다."
        }


        //여기 부분은 어느 정도 자율
    }


    /***
     * functioncall용 전처리
     * gpt 함수호출을 위한 파트 끝부분입니다.
     * ***/
}