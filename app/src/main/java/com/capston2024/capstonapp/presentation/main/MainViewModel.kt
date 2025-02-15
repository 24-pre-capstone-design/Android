package com.capston2024.capstonapp.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.FragmentMode
import com.capston2024.capstonapp.data.requestDto.RequestOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.domain.repository.AuthRepository
import com.capston2024.capstonapp.extension.MenuState
import com.capston2024.capstonapp.extension.OrderState
import com.capston2024.capstonapp.extension.PaymentIdState
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

   // private var orderCheckDialogCallback:OrderCheckDialogCallback?=null
    private var hasPaymentIdBeenSet:Boolean ?= null

    //paymentId
    private val _paymentIdState = MutableStateFlow<PaymentIdState>(PaymentIdState.Loading)
    val paymentIdState: StateFlow<PaymentIdState> = _paymentIdState

    private var paymentId: Int ?= null

    private val _isBagShow = MutableLiveData<Boolean>(false)
    var isBagShow:LiveData<Boolean> = _isBagShow

    //주문히스토리 만들 때 쓰이는 state
    private val _orderState = MutableStateFlow<OrderState>(OrderState.Loading)
    val orderState:StateFlow<OrderState> = _orderState

    //foodCategory 만들기
    fun getMenu() {
        viewModelScope.launch {
            authRepository.getMenuList().onSuccess { response ->
                _menuState.value = MenuState.Success(response.makeMenuList()) // emit 대신 value 사용
                _firstMenu.value = response.makeMenuList()[0]
            }.onFailure {
                if (it is HttpException) {
                    val data = it.response()?.errorBody()?.byteString()?.toString()
                    val errorBody = data?.substringAfter("message")
                }
            }
        }
    }

    //paymentid 설정 및 주문
    fun setPaymentId(name:String) {
        //주문내역 화면이고 paymenrId가 아직 없다면 그냥 return
        if(name.equals("order")&&hasPaymentIdBeenSet==null){
            return
        }

        // hasPaymentIdBeenSet가 true일 때는 이미 paymentId가 설정되었으므로, 더 이상 서버에서 가져오지 않고 주문
        if (hasPaymentIdBeenSet==true) {
            _paymentIdState.value=PaymentIdState.Success(paymentId!!)
            makeOrderHistory()
            return
        }else{

            ///paymenrId가 없으므로 가져오고 서버에서 성공적으로 가져왔다면 주문
            viewModelScope.launch {
                authRepository.getPaymentId().onSuccess { response ->

                    // paymentId를 성공적으로 받아왔으므로, hasPaymentIdBeenSet를 true로 설정하고 LiveData를 업데이트
                    hasPaymentIdBeenSet = true
                    _paymentIdState.value = PaymentIdState.Success(response.data)
                    paymentId = response.data
                    makeOrderHistory()
                    //orderCheckDialogCallback?.handleOrderDetails(response.data)
                }.onFailure {
                    if (it is HttpException) {
                        val data = it.response()?.errorBody()?.byteString()?.toString()
                        val errorBody = data?.substringAfter("message")
                    }
                }
            }
        }
    }

    init {
        _bagList.value = mutableListOf()  // 초기화: 빈 MutableList로 설정
        _mode.value = FragmentMode.AI_MODE
    }

    fun addToBagList(items: Bag) {
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

                if (currentItem.count > quantity) {
                    currentItem.count -= quantity
                } else {
                    updatedList.removeAt(itemIndex)
                }

                // LiveData에 변경된 리스트를 새로 할당하여 업데이트를 감지하게 함
                _bagList.postValue(updatedList)
            }
        }
    }

    //장바구니 내역 주문내역 서버로 보내기
    fun makeOrderHistory(){
        val list = _bagList.value?.map { bag ->
            RequestOrderDto.OrderRequestDtoList(bag.id, bag.count)
        } ?: listOf()

        var request = RequestOrderDto(paymentId!!, list)
        viewModelScope.launch {
            //주문하기. orderhistory에 주문내역을 보냄
            authRepository.makeOrder(request).onSuccess {
                _orderState.value=OrderState.Success
                _bagList.value!!.clear()
            }.onFailure {
                if (it is HttpException) {
                    val data = it.response()?.errorBody()?.byteString()?.toString()
                    val errorBody = data?.substringAfter("message")
                }
            }
        }
    }

    fun updateBagList(updatedList:List<Bag>){
        _bagList.value = updatedList.toMutableList()
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
        _isBagShow.postValue(bagShow)
    }

    fun setOrderStateLoading(){_orderState.value=OrderState.Loading}
}