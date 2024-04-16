package com.capston2024.capstonapp.presentation.course

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
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.FragmentCourseBinding
import com.capston2024.capstonapp.extension.UiState
import com.capston2024.capstonapp.presentation.main.BagFragment
import com.capston2024.capstonapp.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CourseFragment : Fragment() {
    private var _binding: FragmentCourseBinding? = null
    private val binding: FragmentCourseBinding
        get() = requireNotNull(_binding) { "null" }

    private val courseViewModel: CourseViewModel by viewModels()
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseAdapter = CourseAdapter()
        binding.rvFoods.adapter = courseAdapter
        showPhotos()
        setClickListener()
    }

    fun showPhotos() {
        courseViewModel.getData(1)
        lifecycleScope.launch {
            courseViewModel.getState.collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        //Toast.makeText(activity, "정보 가져오기 성공", Toast.LENGTH_SHORT).show()
                        courseAdapter.submitList(uiState.mock)
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

    private fun setClickListener() {
        // 아이템 클릭 리스너 설정
        courseAdapter.setOnItemClickListener(object : CourseViewHolder.OnItemClickListener {
            override fun onFoodItemClick(foodItem: ResponseMockDto.MockModel) {
                // 아이템을 BagFragment로 전달
                //Toast.makeText(activity, "${foodItem.lastName}", Toast.LENGTH_SHORT).show()
                Log.d("courseclick", "course clicked item: ${foodItem.lastName}")
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

    fun scrollToTop() {
        binding.rvFoods.smoothScrollToPosition(0)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}