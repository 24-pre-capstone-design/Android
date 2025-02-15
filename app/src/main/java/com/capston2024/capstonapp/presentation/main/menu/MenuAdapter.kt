package com.capston2024.capstonapp.presentation.main.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.FragmentMode
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.databinding.ItemMenuBinding
import com.capston2024.capstonapp.presentation.main.ChangeFragmentListener


class MenuAdapter(
    private val context: Context,
    private val listener: ChangeFragmentListener
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private val inflater by lazy { LayoutInflater.from(context) }
    private var menuList: MutableList<ResponseMenuDto.Menu> = mutableListOf()
    private var selectedPosition = 0
    private var mode: FragmentMode = FragmentMode.AI_MODE

    inner class MenuViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menuData: ResponseMenuDto.Menu, position: Int) {
            binding.btnMenu.text = menuData.name // 버튼 텍스트 설정

            // mode 값에 따라 버튼의 배경을 변경
            if (mode == FragmentMode.BASIC_MODE) {
                if (selectedPosition == position) {
                    binding.btnMenu.setBackgroundResource(R.drawable.ic_rectangle_beige)
                } else {
                    binding.btnMenu.background =
                        ContextCompat.getDrawable(context, R.drawable.ic_rectangle_white)
                }
            } else {
                binding.btnMenu.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_rectangle_white)
            }
            // 아이템 클릭 리스너
            binding.btnMenu.setOnClickListener {
                handleButtonClick(position)
            }
        }

        private fun handleButtonClick(position: Int) {
            notifyItemChanged(selectedPosition)
            selectedPosition = position
            notifyItemChanged(selectedPosition) // 현재 선택된 아이템 업데이트
            menuList[position].let { menu ->
                listener.replaceFragment(FragmentMode.BASIC_MODE, menu.id)
                listener.changeMenuID(menu.id)
                listener.replaceMenuName(menu.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(inflater, parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]
        holder.bind(menuItem, position)
    }

    override fun getItemCount() = menuList.size

    fun updateMenuList(newMenuList: List<ResponseMenuDto.Menu>, newMode: FragmentMode) {
        if (mode != newMode)
            mode = newMode // mode 값을 업데이트
        menuList.clear()
        menuList.addAll(newMenuList)
        selectedPosition = 0 // 기본 선택 위치를 초기화
        notifyDataSetChanged() // 모든 아이템을 업데이트
    }

    fun updateMode(newMode: FragmentMode) {
        if (mode != newMode)
            mode = newMode
        notifyDataSetChanged()
    }

    fun setSelectedMenuID(menuID: Int) {
        selectedPosition = menuID - 1
        notifyDataSetChanged()
    }
}