package com.capston2024.capstonapp.presentation.order

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.responseDto.ResponseOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderHistoryDto
import com.capston2024.capstonapp.databinding.ItemOrderBinding

class OrderedItemAdapter(private val context: Context) :
    RecyclerView.Adapter<OrderedItemAdapter.OrderedItemViewHolder>() {
    private val inflater by lazy{ LayoutInflater.from(context)}
    private val orderItemList:MutableList<ResponseOrderHistoryDto.OrderResponseDtoList> = mutableListOf()
    inner class OrderedItemViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: ResponseOrderHistoryDto.OrderResponseDtoList) {
            with(binding) {
                tvName.text = orderItem.foodName
                tvCount.text = context.getString(R.string.bag_count, orderItem.quantity)
                tvPrice.text = context.getString(R.string.bag_price, orderItem.sumOfOrderCost)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedItemViewHolder {
        val binding=ItemOrderBinding.inflate(inflater,parent,false)
        return OrderedItemViewHolder(binding)
    }

    override fun getItemCount(): Int = orderItemList.size

    override fun onBindViewHolder(holder: OrderedItemViewHolder, position: Int) {
       val orderItem = orderItemList[position]
        holder.bind(orderItem)
    }

    fun setOrderItemList(items: List<ResponseOrderHistoryDto.OrderResponseDtoList>) {
        orderItemList.clear()
        orderItemList.addAll(items)
        notifyDataSetChanged()
    }

}