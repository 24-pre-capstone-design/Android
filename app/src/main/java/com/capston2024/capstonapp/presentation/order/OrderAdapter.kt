package com.capston2024.capstonapp.presentation.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderHistoryDto
import com.capston2024.capstonapp.databinding.ItemOrderBinding
import com.capston2024.capstonapp.databinding.ItemOrderHistoryBinding
import com.capston2024.capstonapp.presentation.main.MainViewModel

class OrderAdapter(
    private val context: Context,
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private val inflater by lazy{LayoutInflater.from(context)}
    private var orderHistoryList:MutableList<ResponseOrderHistoryDto.OrderHistoryResponseDtoList> = mutableListOf()
    private lateinit var orderedItemAdapter:OrderedItemAdapter

    inner class OrderViewHolder(private val binding:ItemOrderHistoryBinding)
        : RecyclerView.ViewHolder(binding.root) {
        // ViewHolder 내부에서 View 바인딩 및 데이터 설정
        fun bind(order: ResponseOrderHistoryDto.OrderHistoryResponseDtoList) {
           with(binding){
              tvTime.text=order.orderedAt
               // OrderedItemAdapter 인스턴스 생성 및 설정
               val orderedItemAdapter = OrderedItemAdapter(context)
               rvOrder.adapter = orderedItemAdapter
               rvOrder.setHasFixedSize(true) // 성능 개선을 위해 추가할 수 있
               // OrderedItemAdapter에 데이터 설정
               orderedItemAdapter.setOrderItemList(order.orderResponseDtoList)
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        orderedItemAdapter = OrderedItemAdapter(context)
        val binding=ItemOrderHistoryBinding.inflate(inflater,parent,false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderHistoryList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orderHistoryList.size

    fun getOrderHistoryList(historyList:List<ResponseOrderHistoryDto.OrderHistoryResponseDtoList>){
        orderHistoryList.clear()
        orderHistoryList.addAll(historyList)
        notifyDataSetChanged()
    }
/*
    fun getTotalPrice(): Int{
        var price=0
        for(i in 0 ..<viewModel.orderList.value?.size!!){
            price+=viewModel.orderList.value?.get(i)?.price!!*viewModel.orderList.value?.get(i)?.count!!
        }
        return price
    }*/
}
