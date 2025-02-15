package com.capston2024.capstonapp.presentation.main.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.capston2024.capstonapp.data.FragmentMode
import com.capston2024.capstonapp.databinding.FragmentMenuBinding
import com.capston2024.capstonapp.extension.MenuState
import com.capston2024.capstonapp.presentation.main.ChangeFragmentListener
import com.capston2024.capstonapp.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = requireNotNull(_binding) { "바인딩 객체 생성 안됨" }

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
        changeMode()
    }


    private fun setting(){
        val modeValue: FragmentMode = mainViewModel.mode.value ?: FragmentMode.AI_MODE // 0은 기본값으로, null일 경우 사용됩니다.
        menuAdapter = changeFragmentLIstener?.let { MenuAdapter(requireContext(), it) }!!
        binding.rvMenu.adapter = menuAdapter
        //menuAdapter.setMenuList()
        mainViewModel.getMenu()
        makeButton(modeValue)
    }

    private fun changeMode(){
        mainViewModel.mode.observe(viewLifecycleOwner){mode ->
            menuAdapter.updateMode(mode)
        }
    }

    private fun makeButton(mode:FragmentMode) {
        lifecycleScope.launch {
            mainViewModel.menuState.collect { menuState ->
                when (menuState) {
                    is MenuState.Success -> {
                        menuAdapter.updateMenuList(menuState.menuList, mode)
                    }
                    is MenuState.Error -> {
                    }
                    is MenuState.Loading -> {
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