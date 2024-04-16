package com.capston2024.capstonapp.presentation.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.ItemOrderBinding
import com.capston2024.capstonapp.presentation.main.MainViewModel

class OrderAdapter(
    private val context: Context,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private val inflater by lazy{LayoutInflater.from(context)}

    inner class OrderViewHolder(private val binding:ItemOrderBinding)
        : RecyclerView.ViewHolder(binding.root) {
        // ViewHolder 내부에서 View 바인딩 및 데이터 설정
        fun bind(order: Bag) {
           with(binding){
               tvName.text=order.name
               tvCount.text=context.getString(R.string.bag_count, order.count)
               tvPrice.text=context.getString(R.string.bag_price,order.price)
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding=ItemOrderBinding.inflate(inflater,parent,false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        // MainViewModel에서 주문 목록 가져오기
        val orderList = viewModel.orderList.value ?: emptyList()
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = viewModel.orderList.value?.size ?: 0

    fun getTotalPrice(): Int{
        var price=0
        for(i in 0 ..<viewModel.orderList.value?.size!!){
            price+=viewModel.orderList.value?.get(i)?.price!!*viewModel.orderList.value?.get(i)?.count!!
        }
        return price
    }
}
