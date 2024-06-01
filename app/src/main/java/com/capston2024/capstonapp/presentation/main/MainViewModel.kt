package com.capston2024.capstonapp.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.FragmentMode
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.domain.repository.AuthRepository
import com.capston2024.capstonapp.extension.MenuState
import com.capston2024.capstonapp.extension.PaymentIdState
import com.capston2024.capstonapp.presentation.aimode.OpenAIWrapper
import com.capston2024.capstonapp.presentation.main.bag.OrderCheckDialogCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _bagList = MutableLiveData<MutableList<Bag>>()
    val bagList: LiveData<MutableList<Bag>>
        get() = _bagList

    private val _firstMenu = MutableLiveData<ResponseMenuDto.Menu>()
    val firstMenu: LiveData<ResponseMenuDto.Menu> = _firstMenu

    // MutableSharedFlow에서 MutableStateFlow로 변경
    private val _menuState = MutableStateFlow<MenuState>(MenuState.Loading) // 초기 상태로 Loading 설정
    val menuState: StateFlow<MenuState> = _menuState

    //ai모드인지 basic모드인지
    private val _mode = MutableLiveData<FragmentMode>()
    val mode: LiveData<FragmentMode> get() = _mode

    //menuid의 값 저장
    private val _menuID = MutableLiveData<Int>()
    var menuID: LiveData<Int> = _menuID

    //주문내역을 보이게 할지 말지 결정
    private val _order = MutableLiveData<Boolean>(false)
    var order: LiveData<Boolean> = _order

    //직전의 배너 상단의 이름 저장
    var eveTitle: String = ""

    private var orderCheckDialogCallback:OrderCheckDialogCallback?=null
    private var hasPaymentIdBeenSet:Boolean ?= null

    //paymentId
    private val _paymentIdState = MutableStateFlow<PaymentIdState>(PaymentIdState.Loading)
    val paymentIdState: StateFlow<PaymentIdState> = _paymentIdState

    private var paymentId: Int ?= null

    private val _isBagShow = MutableLiveData<Boolean>(false)
    var isBagShow:LiveData<Boolean> = _isBagShow
    //foodCategory 만들기
    fun getMenu() {
        viewModelScope.launch {
            authRepository.getMenuList().onSuccess { response ->
                _menuState.value = MenuState.Success(response.makeMenuList()) // emit 대신 value 사용
                //Log.d("Success","Success~~:mainviewmodel")
                _firstMenu.value = response.makeMenuList()[0]
                Log.d("mainviewmodel","firstmenu: ${_firstMenu.value?.name}")
            }.onFailure {
                Log.e("Error", "Error:${it.message}")
                if (it is HttpException) {
                    val data = it.response()?.errorBody()?.byteString()?.toString()
                    val errorBody = data?.substringAfter("message")
                    if (errorBody != null) {
                        Log.e("mainviewmodel", "message${errorBody}")
                    }
                }
            }
        }
    }
    fun setOrderCheckDialogCallback(callback: OrderCheckDialogCallback) {
        this.orderCheckDialogCallback = callback
    }

    fun setPaymentId(name:String) {
        Log.d("mainviewmodel","set paymentId:")
        if(name.equals("order")&&hasPaymentIdBeenSet==null){
            Log.d("mainviewmodel","paymentId: $paymentId")
            return
        }
        // hasPaymentIdBeenSet가 true일 때는 이미 paymentId가 설정되었으므로, 더 이상 서버에서 가져오지 않음
        if (hasPaymentIdBeenSet==true) {
            orderCheckDialogCallback?.handleOrderDetails(paymentId!!)
            Log.d("mainviewmodel","paymentId: $paymentId")
            return
        }else{
            Log.d("mainviewmodel", "haspaymentidbeenset is false")
            viewModelScope.launch {
                Log.d("mainviewmodel", "viewModelScope.launch 시작")
                authRepository.getPaymentId().onSuccess { response ->
                    // paymentId를 성공적으로 받아왔으므로, hasPaymentIdBeenSet를 true로 설정하고 LiveData를 업데이트
                    hasPaymentIdBeenSet = true
                    _paymentIdState.value = PaymentIdState.Success(response.data)
                    paymentId = response.data
                    orderCheckDialogCallback?.handleOrderDetails(response.data)
                    Log.d("mainviewmodel", "paymentId: ${paymentId}, peymentidstate is ${_paymentIdState.value}")
                }.onFailure {
                    Log.e("mainviewmodel", "Error:${it.message}")
                    if (it is HttpException) {
                        val data = it.response()?.errorBody()?.byteString()?.toString()
                        val errorBody = data?.substringAfter("message")
                        if (errorBody != null) {
                            Log.e("mainviewmodel", "paymentId error: ${errorBody}")
                        }
                    }
                }
            }
        }
    }
    /*fun getHasPaymentIdBeenset(): Boolean = hasPaymentIdBeenSet
    fun getPaymentId():Int = paymentId!!*/

    init {
        _bagList.value = mutableListOf()  // 초기화: 빈 MutableList로 설정
        _mode.value = FragmentMode.AI_MODE
    }

    fun addToBagList(items: Bag) {
        Log.d("mainviewmodel", "mainviewmodel: fooditem-${items.count}")
        val currentList = _bagList.value ?: mutableListOf()
        for(i in 0 until  currentList.size){
            if(items.name.equals(currentList[i].name)){
                currentList[i].count+=items.count
                _bagList.postValue(currentList)
                return
            }
        }
        currentList.add(items)
        _bagList.value = currentList
    }

    //입력받은 음식정보가 baglist에 들어있다면 baglist의 개수와 입력받은 개수를 비교하여
    //입력받은 개수가 baglist에서의 음식 개수보다 작으면 baglist에서 음식 개수를 줄이고
    //아니면 baglist에서 해당 음식을 삭제
    fun deleteFromBagList(items:Bag, quantity:Int){
        _bagList.value?.let { currentList ->
            val updatedList = mutableListOf<Bag>().apply {
                addAll(currentList)
            }
            val itemIndex = updatedList.indexOfFirst { it.name == items.name }

            if (itemIndex != -1) {
                val currentItem = updatedList[itemIndex]

                Log.d("mainviewmodel", "items: ${items.name}, bag: $currentItem")
                if (currentItem.count > quantity) {
                    currentItem.count -= quantity
                    Log.d("mainviewmodel", "minus ${currentItem.count}")
                } else {
                    updatedList.removeAt(itemIndex)
                    Log.d("mainviewmodel", "bag remove! ${updatedList.size}")
                }

                // LiveData에 변경된 리스트를 새로 할당하여 업데이트를 감지하게 함
                _bagList.postValue(updatedList)
            }
        }
    }

    fun updateBagList(updatedList:MutableList<Bag>){
        _bagList.value=updatedList
    }

    fun clearBagList() {
        _bagList.value?.clear()
    }

    fun changeMode(modeNum: FragmentMode) {
        _mode.value = modeNum
    }

    fun changeMenuID(menuID: Int) {
        _menuID.value = menuID
    }

    fun isVisibleOrderList(visible: Boolean) {
        _order.value = visible
    }

    fun setBagShow(bagShow:Boolean){
        _isBagShow.value=bagShow
    }
}