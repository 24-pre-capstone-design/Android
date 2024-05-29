package com.capston2024.capstonapp.presentation.main.menu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.FragmentType
import com.capston2024.capstonapp.databinding.FragmentMenuBinding
import com.capston2024.capstonapp.extension.MenuState
import com.capston2024.capstonapp.presentation.main.ChangeFragmentListener
import com.capston2024.capstonapp.presentation.main.MainViewModel
import com.capston2024.capstonapp.presentation.order.OrderFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = requireNotNull(_binding) { "바인딩 객체 생성 안됨" }

    private val menuViewModel:MenuViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var menuAdapter: MenuAdapter
    private var changeFragmentLIstener:ChangeFragmentListener?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ChangeFragmentListener){
            changeFragmentLIstener=context
        }else{
            throw RuntimeException("$context must implement ChangeFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
        //changeMenu()
        changeMode()
    }


    private fun setting(){
        val modeValue: FragmentType = mainViewModel.mode.value ?: FragmentType.AI_MODE // 0은 기본값으로, null일 경우 사용됩니다.
        menuAdapter = changeFragmentLIstener?.let { MenuAdapter(requireContext(), it) }!!
        binding.rvMenu.adapter = menuAdapter
        //menuAdapter.setMenuList()
        mainViewModel.getMenu()
        makeButton(modeValue)
    }

    private fun changeMenu(){
        mainViewModel.menuID.observe(viewLifecycleOwner){menuID ->
            Log.d("menufragment","menufragment changemenu:${menuID}")
            menuAdapter.setSelectedMenuID(menuID)
        }
        //menuAdapter.setMenuList()
    }
    private fun changeMode(){
        mainViewModel.mode.observe(viewLifecycleOwner){mode ->
            menuAdapter.updateMode(mode)
        }
    }

    private fun makeButton(mode:FragmentType) {
        lifecycleScope.launch {
            mainViewModel.menuState.collect { menuState ->
                when (menuState) {
                    is MenuState.Success -> {
                        Log.d("menufragment","menufragment success")
                        menuAdapter.updateMenuList(menuState.menuList, mode)
                    }
                    is MenuState.Error -> {
                        Log.d("menufragment","menufragment is error")
                    }
                    is MenuState.Loading -> {
                        Log.d("menufragment","menufragment is loading")
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        changeFragmentLIstener=null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}