package com.capston2024.capstonapp.presentation.main

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getString
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.ItemBagBinding
import com.capston2024.capstonapp.databinding.ItemMenuBinding

class BagAdapter(
    private val context: Context,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<BagAdapter.BagViewHolder>() {
    private val inflater by lazy { LayoutInflater.from(context) }
    private var bagList: MutableList<ResponseMockDto.MockModel> = mutableListOf()
    private var countList: MutableList<Int> = mutableListOf()
    private var orderList: MutableList<Bag> = mutableListOf()

    inner class BagViewHolder(private val binding: ItemBagBinding) :
        RecyclerView.ViewHolder(binding.root) {
            //private val fragment=itemView.context as? BagFragment
        init {
            with(binding) {
                btnPlus.setOnClickListener {
                    clickPlusButton(adapterPosition)
                }
                btnMinus.setOnClickListener {
                    clickMinusButton(adapterPosition)
                }
                btnDelete.setOnClickListener {
                    deleteItem(adapterPosition)
                }
            }
        }

        fun bind(bagData: ResponseMockDto.MockModel) {
            binding.tvFoodName.text = bagData.lastName
            binding.tvFoodPrice.text = context.getString(R.string.bag_price, bagData.id)

            val position = adapterPosition
            Log.d("position", "position: ${position}")
            val count = orderList[position].count // 해당 위치의 수량 가져오기.
            binding.tvCount.text = context.getString(R.string.bag_count, count)
        }

        private fun clickPlusButton(position: Int) {
            val currentCount = countList[position] //binding.tvCount.text.toString().substringBefore("개").toInt()
            countList[position] = currentCount + 1
            orderList[position].count=currentCount+1
            val countMessage = context.getString(R.string.bag_count, currentCount + 1)
            binding.tvCount.text = countMessage
            setCount()
            notifyDataSetChanged()
        }

        private fun clickMinusButton(position: Int) {
            val currentCount = countList[position]

            if (currentCount == 1) {
                if (position != RecyclerView.NO_POSITION) {
                    bagList.removeAt(position)
                    countList.removeAt(position)
                    orderList.removeAt(position)
                    Log.d("list size: ", "list size: ${bagList.size}")
                }
                if (bagList.size == 0)
                    deleteBagFragment()
                notifyDataSetChanged()
                return
            }else{
                countList[position]=currentCount-1
                orderList[position].count=currentCount-1
                val countMessage = context.getString(R.string.bag_count, currentCount - 1)
                binding.tvCount.text = countMessage
                Log.d("currentCount", "Current count: ${countList[position]}")
            }
            setCount()
            notifyDataSetChanged()
        }

        private fun deleteItem(position: Int) {
            bagList.removeAt(position)
            countList.removeAt(position)
            orderList.removeAt(position)

            if (bagList.isEmpty()) {
                deleteBagFragment()
            }
            else{
                setCount()
            }
            notifyDataSetChanged()
        }

        private fun setCount(){
            val activity=itemView.context as AppCompatActivity
            val fragment=activity.supportFragmentManager.findFragmentById(R.id.fcv_bag) as BagFragment
            fragment.setCount()
        }

        private fun deleteBagFragment(){
            val activity = context as? AppCompatActivity
            val bagFragment=activity?.supportFragmentManager?.findFragmentById(R.id.fcv_bag) as? BagFragment
            bagFragment?.deleteBagFragment()
        }

        /*fun deleteItem() {
            // bagList가 비어있으면 해당 Fragment를 제거한다.
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val bagFragment = fragmentManager.findFragmentById(R.id.fcv_bag)
            // bagFragment를 제거한다.
            bagFragment?.let {
                val activity = bagFragment.requireActivity() as MainActivity
                activity.bagIsShow = false

                val transaction = fragmentManager.beginTransaction()
                transaction.remove(it)

                transaction.commit()

                // 트랜잭션이 완료될 때까지 기다림
                fragmentManager.executePendingTransactions()

                // 제거된 후에는 bagFragment는 null이 되어야 함
                val isFragmentRemoved = fragmentManager.findFragmentById(R.id.fcv_bag) == null

                Log.d("Fragment", "Fragment removed: $isFragmentRemoved")
            }
            //Log.d("Fragment", "Fragment removed: ${bagFragment == null}")
        }*/
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BagViewHolder {
        val binding = ItemBagBinding.inflate(inflater, parent, false)
        return BagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BagViewHolder, position: Int) {
        val bagItem = bagList[position]
        holder.bind(bagItem)
    }

    override fun getItemCount(): Int = bagList.size

    fun addBagList(bag: ResponseMockDto.MockModel) {
        val existingItem = bagList.find { it.lastName == bag.lastName }
        if (existingItem != null) {
            val existingIndex = bagList.indexOf(existingItem)
            countList[existingIndex] = (countList[existingIndex]) + 1
            orderList[existingIndex].count=(orderList[existingIndex].count)+1
        } else {
            // 새로운 아이템이라면 리스트에 추가
            bagList.add(bag)
            countList.add(1)
            orderList.add(Bag(bag.lastName,bag.id,1))
        }
        notifyDataSetChanged()
    }

    fun getBagList():List<ResponseMockDto.MockModel> = bagList
    fun addOrderList(){
        viewModel.addToOrderList(orderList)
    }
    fun getTotalCount(): Int{
        var total=0
        for(i in orderList)
            total+=i.count
        return total
    }

    fun getTotalPrice(): Int{
        var price=0
        for(i in 0..<orderList.size)
            price+=orderList[i].count*orderList[i].price
        return price
    }


}