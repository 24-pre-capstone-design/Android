package com.capston2024.capstonapp.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.FragmentMenuBinding
import com.capston2024.capstonapp.presentation.order.OrderFragment

class MenuFragment : Fragment() {
    private var _binding:FragmentMenuBinding?=null
    private val binding: FragmentMenuBinding
        get() = requireNotNull(_binding){"바인딩 객체 생성 안됨"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeButton()
    }

    private fun makeButton(){
        val menuAdapter=MenuAdapter(requireContext())
        binding.rvMenu.adapter=menuAdapter
        menuAdapter.setMenuList()

        binding.btnOrderList.setOnClickListener {
            val fragmentManager=requireActivity().supportFragmentManager
            val fragment=fragmentManager.findFragmentById(R.id.fcv_main)
            fragment?.let{
                fragmentManager.beginTransaction()
                    .replace(R.id.fcv_main, OrderFragment())
                    .commit()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}