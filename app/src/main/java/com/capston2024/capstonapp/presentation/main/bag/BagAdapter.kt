package com.capston2024.capstonapp.presentation.main.bag

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.databinding.ItemBagBinding
import java.text.NumberFormat
import java.util.Locale

class BagAdapter(
    private val context: Context,
    private val bagListUpdateListener: BagListUpdateListener
) : RecyclerView.Adapter<BagAdapter.BagViewHolder>() {
    private val inflater by lazy { LayoutInflater.from(context) }
    private var countList: MutableList<Int> = mutableListOf()
    private var bagList: MutableList<Bag> = mutableListOf()

    inner class BagViewHolder(private val binding: ItemBagBinding) :
        RecyclerView.ViewHolder(binding.root) {
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

        fun bind(bagData: Bag) {
            val formatter= NumberFormat.getNumberInstance(Locale.KOREA)
            binding.tvFoodName.text = bagData.name
            binding.tvFoodPrice.text = context.getString(R.string.bag_price, formatter.format(bagData.price))
            Log.d("bagadapter","clicked data: ${bagData.name}")

            val position = adapterPosition
            Log.d("position", "position: ${position}")
            val count = bagList[position].count // 해당 위치의 수량 가져오기.
            binding.tvCount.text = context.getString(R.string.bag_count, bagData.count)
            Log.d("bagadapter","count: ${count}")
        }

        private fun clickPlusButton(position: Int) {
            val currentCount = countList[position] //binding.tvCount.text.toString().substringBefore("개").toInt()
            countList[position] = currentCount + 1
            bagList[position].count=currentCount+1
            val countMessage = context.getString(R.string.bag_count, currentCount + 1)
            binding.tvCount.text = countMessage
            setCount()
            bagListUpdateListener.onBagListUpdated(bagList)
            notifyDataSetChanged()
        }

        private fun clickMinusButton(position: Int) {
            val currentCount = countList[position]

            if (currentCount == 1) {
                if (position != RecyclerView.NO_POSITION) {
                    countList.removeAt(position)
                    bagList.removeAt(position)
                    Log.d("list size: ", "bagadapter list size: ${bagList.size}")
                }
                if (bagList.size == 0)
                    deleteBagFragment()
                bagListUpdateListener.onBagListUpdated(bagList)
                notifyDataSetChanged()
                return
            }else{
                countList[position]=currentCount-1
                bagList[position].count=currentCount-1
                val countMessage = context.getString(R.string.bag_count, currentCount - 1)
                binding.tvCount.text = countMessage
                Log.d("currentCount", "bagadapter Current count: ${countList[position]}")
            }
            setCount()
            bagListUpdateListener.onBagListUpdated(bagList)
            notifyDataSetChanged()
        }

        private fun deleteItem(position: Int) {
            countList.removeAt(position)
            bagList.removeAt(position)

            if (bagList.isEmpty()) {
                deleteBagFragment()
            }
            else{
                setCount()
            }
            bagListUpdateListener.onBagListUpdated(bagList)
            notifyDataSetChanged()
        }

        private fun setCount() {
            val activity = itemView.context as AppCompatActivity
            val fragment =
                activity.supportFragmentManager.findFragmentById(R.id.fcv_bag) as BagFragment
            fragment.setCount()
        }
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

    fun addBagList(bag: Bag) {
        val existingItem = bagList.find { it.name == bag.name }
        if (existingItem != null) {
            val existingIndex = bagList.indexOf(existingItem)
            countList[existingIndex] = (countList[existingIndex]) + 1
            bagList[existingIndex].count=(bagList[existingIndex].count)+1
        } else {
            // 새로운 아이템이라면 리스트에 추가
            countList.add(bag.count)
            bagList.add(Bag(bag.id, bag.name,bag.price,bag.count))
        }
        Log.d("bagadapter","addbaglist, listsize: ${bagList.size}")
        bagListUpdateListener.onBagListUpdated(bagList)
        notifyDataSetChanged()
    }

    private fun deleteBagFragment(){
        val activity = context as? AppCompatActivity
        val bagFragment=activity?.supportFragmentManager?.findFragmentById(R.id.fcv_bag) as? BagFragment
        bagFragment?.deleteBagFragment()
    }

    fun updateList(newList:MutableList<Bag>){
        Log.d("bagadapter","updatelist, listsize: ${bagList.size}")
        bagList=newList
        if(bagList.isEmpty())
            deleteBagFragment()
        notifyDataSetChanged()
    }

    fun getBagList():List<Bag> = bagList

    fun getTotalCount(): Int{
        var total=0
        for(i in bagList)
            total+=i.count
        return total
    }

    fun getTotalPrice(): Int{
        var price=0
        for(i in 0..<bagList.size)
            price+=bagList[i].count*bagList[i].price
        return price
    }
}