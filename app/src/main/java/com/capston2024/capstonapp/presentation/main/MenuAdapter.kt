package com.capston2024.capstonapp.presentation.main

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Menu
import com.capston2024.capstonapp.databinding.ItemMenuBinding
import com.capston2024.capstonapp.presentation.course.CourseFragment
import com.capston2024.capstonapp.presentation.drink.DrinkFragment
import com.capston2024.capstonapp.presentation.handon.HandonFragment
import com.capston2024.capstonapp.presentation.hanoo.HanooFragment
import com.capston2024.capstonapp.presentation.ilpoom.IlpoomFragment

class MenuAdapter(private val context: Context) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private val inflater by lazy { LayoutInflater.from(context) }
    private var menuList: List<Menu> = emptyList()

    var selectedButton: Button?=null
    inner class MenuViewHolder(private val binding:ItemMenuBinding): RecyclerView.ViewHolder(binding.root) {

        init{
            binding.btnMenu.setOnClickListener{
                handleButtonClick(binding.btnMenu)
            }
        }
        fun bind(menuData: Menu){
            binding.btnMenu.text=menuData.menuTitle
            if((selectedButton==null) && (binding.btnMenu.text=="코스요리")){
                selectedButton=binding.btnMenu
                binding.btnMenu.setBackgroundResource(R.drawable.ic_rectangle_beige)
            }
        }
        private fun handleButtonClick(clickedButton:Button){
            Toast.makeText(
                context,
                "${selectedButton?.text}",
                Toast.LENGTH_SHORT
            )
            Log.d("selecedButton", "${selectedButton?.text}")
            // 현재 클릭된 버튼과 이전에 선택된 버튼이 같은지 확인합니다
            if (selectedButton != binding.btnMenu) {
                // 이전에 선택된 버튼의 배경을 원래대로 변경합니다
                selectedButton?.setBackgroundResource(R.drawable.ic_rectangle_white)

                // 현재 클릭된 버튼의 배경을 변경하고 selectedButton을 업데이트합니다
                binding.btnMenu.setBackgroundResource(R.drawable.ic_rectangle_beige)
                selectedButton = binding.btnMenu

                val menuTitle=clickedButton.text.toString()
                // 버튼 클릭 시 해당 fragment를 MainActivity의 replaceFragment로 교체합니다
                when (menuTitle) {
                    "코스요리" -> {(context as MainActivity).replaceFragment(CourseFragment())
                        //courseFragment.showPhotos()
                    }
                    "한돈" -> (context as MainActivity).replaceFragment(HandonFragment())
                    "한우" -> (context as MainActivity).replaceFragment(HanooFragment())
                    "일품" -> (context as MainActivity).replaceFragment(IlpoomFragment())
                    "음료/주류" -> (context as MainActivity).replaceFragment(DrinkFragment())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(inflater, parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem=menuList[position]
        holder.bind(menuItem)
    }

    override fun getItemCount() = menuList.size

    fun setMenuList() {
        val menuList = listOf<Menu>(
            Menu("코스요리"),
            Menu("한돈"),
            Menu("한우"),
            Menu("일품"),
            Menu("음료/주류")
        )
        this.menuList = menuList.toList()
        notifyDataSetChanged()
    }

}