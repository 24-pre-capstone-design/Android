package com.capston2024.capstonapp.presentation.aimode

import android.util.Log
import com.aallam.openai.api.chat.Parameters
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class FoodMenuFunctions {

    companion object {
        private val menuItems = mapOf(
            "야채비빔밥" to Pair(10000, "SALE"),
            "육회비빔밥" to Pair(15000, "SALE"),
            "김치찌개" to Pair(10000, "SALE"),
            "된장찌개" to Pair(10000, "SALE"),
            "미역국" to Pair(9000, "SALE"),
            "설렁탕" to Pair(12000, "SALE"),
            "갈비탕" to Pair(12000, "SALE"),
            "소불고기" to Pair(12000, "SALE"),
            "모듬생선구이" to Pair(23000, "SALE"),
            "오리훈제구이" to Pair(11000, "SALE"),
            "간장새우장" to Pair(13000, "SALE"),
            "계란말이" to Pair(5000, "SALE"),
            "낙지볶음" to Pair(14000, "SALE"),
            "돌솥비빔밥" to Pair(11000, "SALE"),
            "돼지김치찜" to Pair(13000, "SALE"),
            "두부조림" to Pair(8000, "SALE"),
            "떡갈비" to Pair(10000, "SALE"),
            "한우떡국" to Pair(8000, "SALE"),
            "만둣국" to Pair(8000, "SALE"),
            "매실차" to Pair(5000, "SALE"),
            "매운갈비찜" to Pair(15000, "SALE"),
            "한방보쌈" to Pair(17000, "SALE"),
            "삼계탕" to Pair(18000, "SALE"),
            "전통식혜" to Pair(6000, "SALE"),
            "영양솥밥" to Pair(12000, "SALE"),
            "한우잡채" to Pair(13000, "SALE"),
            "전복버터구이" to Pair(21000, "SALE"),
            "꽁치김치찌개" to Pair(12000, "SALE"),
            "동태찌개" to Pair(12000, "SALE"),
            "돼지고기김치찌개" to Pair(12000, "SALE")
        )
        fun name(): String {
            return "FoodMenuFunctions"
        }

        fun description(): String {
            return "Return details for a specified food, including price and availability."
        }

        fun params(): Parameters {
            return Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("foodName") {
                        put("type", "string")
                        put("description", "한식다이어리의 메뉴를 입력받고 가격을 반환합니다. 전부 한국어로만 입력을 받습니다. " +
                                "예를 들어 '야채비빔밥'같은 foodName을 입력받으면 10000을 반환할 것 입니다.")
                    }
                }
                putJsonArray("required") {
                    add("foodName")
                }
            }
        }

        /** Implement your method to interact with the database or data source to fetch food details */
        fun getFoodDetails(foodName: String): String {
            // Implementation to fetch food details from data
            // This is a placeholder: replace with actual data fetching logic
            val foodDetails = "Detailed information for $foodName including price and status."
            Log.i("FoodMenuFunctions", foodDetails)
            return foodDetails
        }
    }
}