package com.capston2024.capstonapp.presentation.aimode

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.aallam.openai.api.core.Parameters
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.FragmentMode
import com.capston2024.capstonapp.presentation.main.MainActivity
import com.capston2024.capstonapp.presentation.main.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import java.io.Serializable

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
                if (!mainViewModel.isBagShow.value!!) {
                    val bag = Bag(foodItem.id, foodItem.name, foodItem.price, quantity)
                    mainViewModel.addToBagList(bag)
                    val bundle = Bundle().apply {
                        putSerializable("selectedFood", bag as Serializable)
                    }
                    mainActivity.bagFragment.arguments = bundle
                    //mainViewModel.setBagShow(true)
                    mainActivity.setBagFragment(FragmentMode.AI_MODE)
                    /*mainActivity.showFragments(
                        R.id.fcv_bag,
                        mainActivity.bagFragment,
                        FragmentMode.AI_MODE
                    )*/
                } else {
                    mainActivity.bagFragment.setBag()
                }
                mainActivity.supportFragmentManager.executePendingTransactions()
            }
        } else {
            Log.e("error", "Food item not found: $item")
        }
    }
    fun deleteFoodFromBag(item: String, quantity: Int){
        val foodItem = aiViewModel.getFoodByName(item)// 음식 이름으로 음식 객체를 가져오는 함수
        if(foodItem!=null){
            mainViewModel.deleteFromBagList(Bag(foodItem.id, foodItem.name,foodItem.price,quantity), quantity)
        }else{
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
    }
//---------------
    fun FMFname(): String {
        return "FoodMenuFunction"
    }

    fun FMFdescription(): String {
        return "한식다이어리의 음식에 대한 정보를 반환합니다. " +
                "만약 이미 한번 호출된 적이 있고 한식다이어리의 음식에 대한 정보를 이미 알고 있다면 더 이상 호출할 필요는 없습니다." +
                "사용자는 어떤 음식이 있는지, 이 음식의 가격은 무엇인지 물어볼 수 있습니다." +
                "이때 전체 음식에 대한 정보를 묻는 질문엔 바로 함수를 호출하여 전체 리스트에 대한 정보를 반환합니다." +
                "만약 음식 가격을 물어본다면 foodName에 요청한 음식 이름을 넣고 가격을 반환합니다." +
                "그 밖에도 반환된 리스트를 통해 얻은 정보로 상대방에게 어떤 정보를 가지고 있는지 알립니다." +
                "정보는 음식 종류, 음식 이름, 음식 가격, "

    }

    fun FMFparams(): Parameters {
        val params = Parameters.buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {

            }
            putJsonArray("required") {

            }
        }
        return params
    }



     fun foodMenuFunction(): String {
        val foodList = aiViewModel.getFoodList()
        val foodListString = foodList?.joinToString(separator = "\n") { food ->
            "Category: ${food.foodCategory.name}, Name: ${food.name}, Price: ${food.price}"
        }
        return foodListString.toString()
    }
    //----------------------------------------------------------------------------------------
    fun FDFname(): String {
        return "foodDeleteFunction"
    }

    fun FDFdescription(): String {
        return "음식을 주문취소하는 함수 입니다." +
                "음식 이름과 개수를 입력 받아 장바구니에서 삭제합니다. " +
                "만약 장바구니에 해당 음식이 없거나 해당 수량만큼 존재하지 않으면 장바구니에 이미 없는 내용이라고 반환" +
                "만약 음식 이름만 언급했다면 quantity는 1로 간주합니다. " +
                "예를 들어 '김치찌개 취소할게' 라고 입력받으면 장바구니에서 김치찌개를 삭제합니다."

    }

    fun FDFparams(): Parameters {
        val params = Parameters.buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("foodName") {
                    put("type", "string")
                    put("description", "주문 취소할 음식 이름 입니다. ")
                }
                putJsonObject("quantity") {
                    put("type", "string")
                    put("description", "주문 취소할 양 입니다. 기본값은 1입니다.")
                }
            }
            putJsonArray("required") {
                add("foodName")
            }
        }
        return params
    }



    fun foodDeleteFunction(foodName:String, quantity: String = "1"): String {
        //음식 장바구니에서 제거하는 함수 입니다.
        return ""
    }
    //==============================================================================
    fun OFname(): String {
        return "orderFunction"
    }

    fun OFdescription(): String {
        return "먼저 주문받은 것들은 장바구니에 들어있습니다." +
                "이 함수는 장바구니에 저장된 음식을 최종 주문하여 주문내역에 추가하는 함수 입니다."

    }

    fun OFparams(): Parameters {
        val params = Parameters.buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {

            }
            putJsonArray("required") {

            }
        }
        return params
    }



    fun orderFunction(): String {
        //주문내역에 추가하는 함수 입니다.
        return ""
    }
    //----------------------------------------------------------------------------------------
    fun FDFname(): String {
        return "foodDeleteFunction"
    }

    fun FDFdescription(): String {
        return "음식을 주문취소하는 함수 입니다." +
                "음식 이름과 개수를 입력 받아 장바구니에서 삭제합니다. " +
                "만약 장바구니에 해당 음식이 없거나 해당 수량만큼 존재하지 않으면 장바구니에 이미 없는 내용이라고 반환" +
                "만약 음식 이름만 언급했다면 quantity는 1로 간주합니다. " +
                "예를 들어 '김치찌개 취소할게' 라고 입력받으면 장바구니에서 김치찌개를 삭제합니다."

    }

    fun FDFparams(): Parameters {
        val params = Parameters.buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("foodName") {
                    put("type", "string")
                    put("description", "주문 취소할 음식 이름 입니다. ")
                }
                putJsonObject("quantity") {
                    put("type", "string")
                    put("description", "주문 취소할 양 입니다. 기본값은 1입니다.")
                }
            }
            putJsonArray("required") {
                add("foodName")
            }
        }
        return params
    }



    fun foodDeleteFunction(foodName:String, quantity: String = "1"): String {
        //음식 장바구니에서 제거하는 함수 입니다.
        val foodItem = aiViewModel.getFoodByName(foodName)// 음식 이름으로 음식 객체를 가져오는 함수
        if(foodItem!=null){
            mainViewModel.deleteFromBagList(Bag(foodItem.id, foodItem.name,foodItem.price,quantity.toInt()), quantity.toInt())
            Log.d("cartmanager","fooditem is not null")
        }else{
            Log.e("error", "Food item not found: $foodName")
        }
        return ""
    }
    /***
     * functioncall용 전처리
     * gpt 함수호출을 위한 파트 끝부분입니다.
     * ***/
    /***
     * functioncall용 전처리
     * gpt 함수호출을 위한 파트 끝부분입니다.
     * ***/
}
data class Menu(val id: Int, val name: String)

data class Food(
    val id: Int,
    val foodCategory: Menu,
    val name: String,
    val price: Int,
    val pictureURL: String,
    val status: String,
    val createdAt: String
)