package com.capston2024.capstonapp.presentation.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
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
    var bagFragment = BagFragment()
    var aiFragment=AIFragment()

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
        with(mainViewModel){
            getMenu()
            changeMode(FragmentType.AI_MODE)
            eveTitle=getString(R.string.main_aimode)
        }

        with(binding){
            btnAiMode.setOnClickListener {
                if (binding.btnAiMode.text.equals(getString(R.string.start_ai))) {
                    replaceFragment(aiFragment, FragmentType.AI_MODE)
                } else {
                    val firstFoodsIDValue = mainViewModel.firstMenu.value?.id ?: 0
                    replaceFragment(FoodsFragment(firstFoodsIDValue), FragmentType.BASIC_MODE)
                    replaceMenuFragment(MenuFragment())
                }
            }
            btnOrderList.setOnClickListener {
                mainViewModel.isVisibleOrderList(true)
            }
            tvMenuName.text=mainViewModel.eveTitle
        }

        showFragments(R.id.fcv_menu, MenuFragment(), FragmentType.AI_MODE)
        showFragments(R.id.fcv_main, aiFragment,  FragmentType.AI_MODE)
        showFragments(R.id.fcv_order, OrderFragment(),  FragmentType.AI_MODE)
    }

    fun showFragments(fcv: Int, fragment: Fragment, type: FragmentType) {
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

    private fun detachChange() {
        mainViewModel.mode.observe(this) { mode ->
            when (mode) {
                FragmentType.AI_MODE -> {
                    binding.btnAiMode.text = getString(R.string.start_basic)
                    binding.tvMenuName.text = getString(R.string.main_aimode)
                    //bagfragment가 보여지고 있다면 숨김
                    if(mainViewModel.isBagShow.value!!){
                        adjustShowingBagFragment(!mainViewModel.isBagShow.value!!)
                    }
                }

                else -> {
                    binding.btnAiMode.text = getString(R.string.start_ai)
                    binding.tvMenuName.text = mainViewModel.firstMenu.value?.name
                    //bagfragment가 보여지고 있었다면 보여줌
                    if(mainViewModel.isBagShow.value!!){
                        adjustShowingBagFragment(mainViewModel.isBagShow.value!!)
                    }
                }
            }
        }
        mainViewModel.order.observe(this) { visible ->
            when (visible) {
                true -> {
                    mainViewModel.eveTitle = binding.tvMenuName.text.toString()
                    with(binding) {
                        fcvOrder.visibility = View.VISIBLE
                        tvMenuName.text = getString(R.string.order_title)
                        btnOrderList.visibility = View.INVISIBLE
                        btnAiMode.visibility = View.INVISIBLE
                    }
                    //bagfragment가 보여지고 있다면 숨김
                    if(mainViewModel.isBagShow.value!!){
                        adjustShowingBagFragment(!mainViewModel.isBagShow.value!!)
                    }
                    Log.d("mainactivity","eveTitle: ${mainViewModel.eveTitle}")
                }

                else -> {
                    with(binding) {
                        fcvOrder.visibility = View.INVISIBLE
                        tvMenuName.text = mainViewModel.eveTitle
                        btnOrderList.visibility=View.VISIBLE
                        btnAiMode.visibility=View.VISIBLE
                    }
                    //기본모드였고, bagfragment가 보여지고 있었다면 보여줌
                    if(mainViewModel.isBagShow.value!!&&mainViewModel.mode.value==FragmentType.BASIC_MODE){
                        adjustShowingBagFragment(mainViewModel.isBagShow.value!!)
                    }
                }
            }
        }
    }

    private fun replaceMenuFragment(fragment:Fragment){
        supportFragmentManager.popBackStackImmediate()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_menu, fragment)
            .commit()
    }

    private fun adjustShowingBagFragment(showBag:Boolean) {
        if(showBag){
            val fragment = supportFragmentManager.findFragmentById(R.id.fcv_bag)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().show(fragment).commit()
            }
        }else{
            val fragment = supportFragmentManager.findFragmentById(R.id.fcv_bag)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().hide(fragment).commit()
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