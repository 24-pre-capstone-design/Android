package com.capston2024.capstonapp.presentation.aimode

import com.aallam.openai.api.core.Parameters
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.collections.mutableMapOf

class CartManager {

    companion object{
        val cartItems: MutableMap<String, Int> = mutableMapOf()
        fun name(): String{
            return "CartManager"
        }
        fun description(): String {
            return "현재 사용자의 주문을 관리합니다. 주문하면 장바구니에 음식을 추가하거나 뺄수 있고 현재 얼마나 들어있는지 호출할 수 있습니다."
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
        suspend fun function(item: String, quantity: String):String{
            val quantityInt = quantity.toInt()

            if (cartItems.containsKey(item)) {
                cartItems[item] = (cartItems[item] ?: 0) + quantityInt
                return "장바구니에 "+item+"을 추가했습니다!"
            } else {
                cartItems[item] = quantityInt
                return "장바구니에 "+item+" "+quantity+"개를 추가했습니다!"
            }
            return "잘 입력되지 않음!"
        }

    }





}