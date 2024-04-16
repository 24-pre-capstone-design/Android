package com.capston2024.capstonapp.presentation.drink

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.FragmentDrinkBinding
import com.capston2024.capstonapp.extension.UiState
import com.capston2024.capstonapp.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DrinkFragment:Fragment() {
    private var _binding: FragmentDrinkBinding?=null
    private val binding: FragmentDrinkBinding
        get() = requireNotNull(_binding){"바인딩 객체 생성 안됨"}

    private val drinkViewModel: DrinkViewModel by viewModels()
    private lateinit var drinkAdapter: DrinkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrinkBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drinkAdapter= DrinkAdapter()
        binding.rvFoods.adapter=drinkAdapter
        showPhotos()
        setClickListener()
    }

    fun showPhotos(){
        drinkViewModel.getData(5)
        lifecycleScope.launch {
            drinkViewModel.getState.collect{uiState ->
                when(uiState){
                    is UiState.Success -> {
                        //Toast.makeText(activity, "정보 가져오기 성공", Toast.LENGTH_SHORT).show()
                        drinkAdapter.submitList(uiState.mock)
                    }

                    is UiState.Error -> {
                        //Toast.makeText(activity, "정보 가져오기 실패", Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Loading -> {
                        //Toast.makeText(activity, "로딩중", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setClickListener(){
        drinkAdapter.setOnItemClickListener(object : DrinkViewHolder.OnItemClickListener{
            override fun onFoodItemClick(foodItem: ResponseMockDto.MockModel) {
                Log.d("drinkclick","drink clicked item: ${foodItem.lastName}")
                val activity = requireActivity() as MainActivity
                val bagFragment=activity.bagFragment
                val bundle = Bundle().apply {
                    putSerializable("selectedFood", foodItem)
                }
                bagFragment.arguments=bundle

                if (!activity.bagIsShow)
                    activity.settingFragments(R.id.fcv_bag, bagFragment, "bagFragment")
                else {
                    bagFragment.setBag()
                }
                activity.supportFragmentManager.executePendingTransactions()
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}