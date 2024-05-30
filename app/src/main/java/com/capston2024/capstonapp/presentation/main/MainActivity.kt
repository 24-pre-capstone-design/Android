package com.capston2024.capstonapp.presentation.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.FragmentType
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.databinding.ActivityMainBinding
import com.capston2024.capstonapp.extension.MenuState
import com.capston2024.capstonapp.presentation.startend.PayingActivity
import com.capston2024.capstonapp.presentation.aimode.AIFragment
import com.capston2024.capstonapp.presentation.main.bag.BagFragment
import com.capston2024.capstonapp.presentation.main.foods.FoodsFragment
import com.capston2024.capstonapp.presentation.main.menu.MenuFragment
import com.capston2024.capstonapp.presentation.order.OrderFragment
import com.capston2024.capstonapp.presentation.order.PaymentListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PaymentListener, ChangeFragmentListener {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    var bagIsShow: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (newValue) {
            changeArrangement()
        } else {
            backArrangement()
        }
    }

    fun changeArrangement() {
        //fcv_main 내부의 fragment
        val fragment = supportFragmentManager.findFragmentById(R.id.fcv_main) as FoodsFragment
        //fragment의 배열 변경
        fragment.changeSpanCount(2)
    }

    fun backArrangement() {
        //fcv_main 내부의 fragment
        val fragment = supportFragmentManager.findFragmentById(R.id.fcv_main) as FoodsFragment
        //fragment의 배열 변경
        fragment.changeSpanCount(3)
    }

    var bagFragment = BagFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinds()
        setFragments()
        detachChange()
    }

    private fun initBinds() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setFragments() {
        mainViewModel.getMenu()
        showFragments(R.id.fcv_menu, MenuFragment(), "menuFragment", FragmentType.AI_MODE)

        var modeName = intent.getStringExtra("mode") // mode를 가져옴
        val mode = if (modeName != null) FragmentType.valueOf(modeName) else FragmentType.AI_MODE

        if (mode.equals(FragmentType.AI_MODE)) {
            // ai mode 처리
            showFragments(R.id.fcv_main, AIFragment(), "aiFragment", FragmentType.AI_MODE)
            mainViewModel.changeMode(FragmentType.AI_MODE)
        } else if (mode.equals(FragmentType.BASIC_MODE)) {
            // basic mode 처리
            getFoodFragment()
            mainViewModel.changeMode(FragmentType.BASIC_MODE)
        }

        binding.btnAiMode.setOnClickListener {
            if (binding.btnAiMode.text.equals(getString(R.string.start_ai))) {
                replaceFragment(AIFragment(), FragmentType.AI_MODE)
            } else {
                val firstFoodsIDValue = mainViewModel.firstMenu.value?.id ?: 0
                replaceFragment(FoodsFragment(firstFoodsIDValue), FragmentType.BASIC_MODE)
            }
        }

        binding.btnOrderList.setOnClickListener {
            mainViewModel.isVisibleOrderList(true)
        }
        showFragments(R.id.fcv_order, OrderFragment(), "orderFragment", FragmentType.AI_MODE)
    }

    fun showFragments(fcv: Int, fragment: Fragment, name: String, type: FragmentType) {
        val currentFragment = supportFragmentManager.findFragmentById(fcv)
        if (currentFragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(fcv, fragment)
                .commit()
        }

        if (fcv == R.id.fcv_main) {
            //type == 0 -> ai모드니까 기본모드버튼, type != 0 -> 기본모드니까 ai모드버튼
            if (type == FragmentType.AI_MODE) {
                binding.btnAiMode.text = getString(R.string.start_basic)
                mainViewModel.changeMode(FragmentType.AI_MODE)
            } else {
                binding.btnAiMode.text = getString(R.string.start_ai)
                mainViewModel.changeMode(FragmentType.BASIC_MODE)
            }
        }
    }

    //foodfragment의 내용 가져오기
    private fun getFoodFragment() {
        lifecycleScope.launch {
            mainViewModel.menuState.collect { menuState ->
                when (menuState) {
                    is MenuState.Success -> {
                        showFragments(
                            R.id.fcv_main,
                            FoodsFragment(menuState.menuList[0].id),
                            "mainFragment",
                            FragmentType.BASIC_MODE
                        )
                        replaceMenuName(menuState.menuList[0].name)
                    }

                    is MenuState.Error -> {
                    }

                    is MenuState.Loading -> {
                    }
                }
            }
        }
    }

    private fun detachChange() {
        mainViewModel.mode.observe(this) { mode ->
            when (mode) {
                FragmentType.AI_MODE -> {
                    binding.btnAiMode.text = getString(R.string.start_basic)
                    binding.tvMenuName.text = getString(R.string.main_aimode)
                }

                else -> {
                    binding.btnAiMode.text = getString(R.string.start_ai)
                    binding.tvMenuName.text = mainViewModel.firstMenu.value?.name
                }
            }
        }
        mainViewModel.order.observe(this) { visible ->
            when (visible) {
                true -> {
                    with(binding){
                        fcvOrder.visibility = View.VISIBLE
                        tvMenuName.text = getString(R.string.order_title)
                        btnOrderList.visibility = View.INVISIBLE
                        btnAiMode.visibility=View.INVISIBLE
                    }
                    mainViewModel.eveTitle = binding.tvMenuName.text.toString()
                }

                else -> {
                    with(binding) {
                        fcvOrder.visibility = View.INVISIBLE
                        tvMenuName.text = mainViewModel.eveTitle
                        btnOrderList.visibility=View.VISIBLE
                        btnAiMode.visibility=View.VISIBLE
                    }

                }
            }
        }
    }

    override fun CompletePayment() {
        var intent = Intent(this, PayingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun replaceFragment(fragment: Fragment, type: FragmentType) {
        supportFragmentManager.popBackStackImmediate()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_main, fragment)
            .commit()

        //mode 입력
        mainViewModel.changeMode(type)
    }

    override fun replaceMenuName(menuName: String) {
        binding.tvMenuName.text = menuName
    }

    override fun changeMenuID(menuID: Int) {
        mainViewModel.changeMenuID(menuID)
    }
}