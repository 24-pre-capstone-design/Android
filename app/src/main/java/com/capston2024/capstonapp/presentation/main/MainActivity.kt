package com.capston2024.capstonapp.presentation.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.FragmentMode
import com.capston2024.capstonapp.databinding.ActivityMainBinding
import com.capston2024.capstonapp.presentation.aimode.AIFragment
import com.capston2024.capstonapp.presentation.aimode.AIViewModel
import com.capston2024.capstonapp.presentation.main.bag.BagFragment
import com.capston2024.capstonapp.presentation.main.foods.FoodsFragment
import com.capston2024.capstonapp.presentation.main.menu.MenuFragment
import com.capston2024.capstonapp.presentation.order.OrderFragment
import com.capston2024.capstonapp.presentation.order.PaymentListener
import com.capston2024.capstonapp.presentation.startend.CompletePaymentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PaymentListener, ChangeFragmentListener {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()
    private val aiViewModel: AIViewModel by viewModels()
    var bagFragment = BagFragment()
    val aiFragment = AIFragment()
    private var foodsFragment: FoodsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinds()
        setFragments()
        detachChange()
    }

    private fun initBinds() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setFragments() {

        with(mainViewModel) {
            getMenu()
            changeMode(FragmentMode.AI_MODE)
            eveTitle = getString(R.string.main_aimode)
        }

        with(binding) {
            btnChangeMode.setOnClickListener {
                val transaction = supportFragmentManager.beginTransaction()

                if (binding.btnChangeMode.text == getString(R.string.start_ai)) {
                    if (!aiFragment.isAdded) {
                        transaction.add(R.id.fcv_main, aiFragment, "aiFragment")
                    }
                    transaction.show(aiFragment)
                    foodsFragment?.let { transaction.remove(it) }
                    foodsFragment = null
                    mainViewModel.changeMode(FragmentMode.AI_MODE)
                } else {
                    foodsFragment =
                        supportFragmentManager.findFragmentByTag("foodsFragment") as? FoodsFragment

                    val firstFoodsIDValue = mainViewModel.firstMenu.value?.id ?: 0
                    foodsFragment = FoodsFragment(firstFoodsIDValue)
                    mainViewModel.changeMenuID(firstFoodsIDValue)
                    transaction.add(R.id.fcv_main, foodsFragment!!, "foodsFragment")
                    /*} else {
                        val firstFoodsIDValue = mainViewModel.firstMenu.value?.id ?: 0
                        mainViewModel.changeMenuID(firstFoodsIDValue)
                        transaction.show(foodsFragment!!)
                    }*/
                    transaction.hide(aiFragment)
                    mainViewModel.changeMode(FragmentMode.BASIC_MODE)
                    replaceMenuFragment(MenuFragment())
                }
                transaction.commit()

                /*if (binding.btnChangeMode.text.equals(getString(R.string.start_ai))) {
                    //replaceMainFragment(aiFragment, "aiFragment",FragmentMode.AI_MODE)
                    //replaceFragment(aiFragment, FragmentMode.AI_MODE)
                } else {
                    val firstFoodsIDValue = mainViewModel.firstMenu.value?.id ?: 0
                    //replaceMainFragment(FoodsFragment(firstFoodsIDValue),"foodsFragment",FragmentMode.BASIC_MODE)
                    //replaceFragment(FoodsFragment(firstFoodsIDValue), FragmentMode.BASIC_MODE)
                    replaceMenuFragment(MenuFragment())
                }*/
            }
            btnOrderList.setOnClickListener {
                mainViewModel.isVisibleOrderList(true)
            }
            tvMenuName.text = mainViewModel.eveTitle
        }

        showFragments(R.id.fcv_menu, MenuFragment(), FragmentMode.AI_MODE)
        showFragments(R.id.fcv_main, aiFragment, FragmentMode.AI_MODE)
        showFragments(R.id.fcv_order, OrderFragment(), FragmentMode.AI_MODE)
    }

    fun showFragments(fcv: Int, fragment: Fragment, type: FragmentMode) {
        val currentFragment = supportFragmentManager.findFragmentById(fcv)
        if (currentFragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(fcv, fragment)
                .commit()
        }

        if (fcv == R.id.fcv_main) {
            //type == 0 -> ai모드니까 기본모드버튼, type != 0 -> 기본모드니까 ai모드버튼
            if (type == FragmentMode.AI_MODE) {
                binding.btnChangeMode.text = getString(R.string.start_basic)
                mainViewModel.changeMode(FragmentMode.AI_MODE)
            } else {
                binding.btnChangeMode.text = getString(R.string.start_ai)
                mainViewModel.changeMode(FragmentMode.BASIC_MODE)
            }
        }
    }

    private fun detachChange() {
        mainViewModel.mode.observe(this) { mode ->
            when (mode) {
                FragmentMode.AI_MODE -> {
                    binding.btnChangeMode.text = getString(R.string.start_basic)
                    binding.tvMenuName.text = getString(R.string.main_aimode)
                    //bagfragment가 보여지고 있다면 숨김
                    if (mainViewModel.isBagShow.value!!) {
                        adjustShowingBagFragment(!mainViewModel.isBagShow.value!!)
                    }
                }

                else -> {
                    binding.btnChangeMode.text = getString(R.string.start_ai)
                    binding.tvMenuName.text = mainViewModel.firstMenu.value?.name
                    Log.d("mainactivity", "bag show:${mainViewModel.isBagShow.value}")
                    //bagfragment가 보여지고 있었다면 보여줌
                    if (mainViewModel.isBagShow.value!!) {
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
                        btnChangeMode.visibility = View.INVISIBLE
                    }
                    //bagfragment가 보여지고 있다면 숨김
                    if (mainViewModel.isBagShow.value!!) {
                        adjustShowingBagFragment(!mainViewModel.isBagShow.value!!)
                    }
                    Log.d("mainactivity", "eveTitle: ${mainViewModel.eveTitle}")
                }

                else -> {
                    with(binding) {
                        fcvOrder.visibility = View.INVISIBLE
                        tvMenuName.text = mainViewModel.eveTitle
                        btnOrderList.visibility = View.VISIBLE
                        btnChangeMode.visibility = View.VISIBLE
                    }
                    //기본모드였고, bagfragment가 보여지고 있었다면 보여줌
                    if (mainViewModel.isBagShow.value!! && mainViewModel.mode.value == FragmentMode.BASIC_MODE) {
                        adjustShowingBagFragment(mainViewModel.isBagShow.value!!)
                    }
                }
            }
        }
    }

    private fun replaceMenuFragment(fragment: Fragment) {
        supportFragmentManager.popBackStackImmediate()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_menu, fragment)
            .commit()
    }

    /*    private fun replaceMainFragment(fragment:Fragment, name:String, mode:FragmentMode){
            val transaction = supportFragmentManager.beginTransaction()

            if (binding.btnChangeMode.text.equals(getString(R.string.start_ai))) {
                if(name.equals("aiFragment")){
                    if (fragment.isAdded) {
                        transaction.show(fragment)
                    } else {
                        transaction.add(R.id.fcv_main, fragment, "aiFragment")
                    }
                }

                val foodsFragment = supportFragmentManager.findFragmentByTag("foodsFragment")
                if (foodsFragment != null) {
                    transaction.hide(foodsFragment)
                }
            } else {
                val firstFoodsIDValue = mainViewModel.firstMenu.value?.id ?: 0
                val foodsFragment = FoodsFragment(firstFoodsIDValue)
                transaction.add(R.id.fcv_main, foodsFragment, "foodsFragment")
                transaction.hide(aiFragment)
            }
            transaction.commit()

            //mode 입력
            mainViewModel.changeMode(mode)
        }*/

    private fun adjustShowingBagFragment(showBag: Boolean) {
        if (showBag) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fcv_bag)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().show(fragment).commit()
            }
        } else {
            val fragment = supportFragmentManager.findFragmentById(R.id.fcv_bag)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().hide(fragment).commit()
            }
        }
    }

    fun setBagFragment(fragmentMode: FragmentMode) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_bag, bagFragment)
            .hide(bagFragment)
            .commit()
    }

    override fun PayingProgress() {
        val payingDL = Dialog(this)
        payingDL.setContentView(R.layout.popup_paying)
        payingDL.setCancelable(false)
        payingDL.show()

        val payingPB: ProgressBar = payingDL.findViewById(R.id.payingProgressBar)
        animateProgressBar(payingPB)

        val circle1: View = payingDL.findViewById(R.id.circle1)
        val circle2: View = payingDL.findViewById(R.id.circle2)
        val circle3: View = payingDL.findViewById(R.id.circle3)
        animateCircle(circle1)
        animateCircle(circle2, delay = 200)
        animateCircle(circle3, delay = 400)

        Handler(Looper.getMainLooper()).postDelayed({
            payingDL.dismiss()
            CompletePayment()
        }, 5000)

    }

    private fun animateProgressBar(progressBar: ProgressBar) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 4700
        animator.start()
    }

    private fun animateCircle(view: View, delay: Long = 0) {

        val animatorSet = AnimatorSet()

        val translateYAnimatorUp = ObjectAnimator.ofFloat(view, "translationY", -5f, 5f)
        translateYAnimatorUp.duration = 300
        translateYAnimatorUp.interpolator = AccelerateDecelerateInterpolator()

        val translateYAnimatorDown = ObjectAnimator.ofFloat(view, "translationY", 5f, -5f)
        translateYAnimatorDown.duration = 300
        translateYAnimatorDown.interpolator = DecelerateInterpolator()

        animatorSet.playSequentially(translateYAnimatorUp, translateYAnimatorDown)
        animatorSet.startDelay = delay
        animatorSet.start()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animatorSet)
                animatorSet.startDelay = 0
                animatorSet.start()

            }
        })

    }

override fun CompletePayment() {
    var intent = Intent(this, CompletePaymentActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
    finish()
}

override fun replaceFragment(type: FragmentMode, id: Int) {
    // foodsFragment = supportFragmentManager.findFragmentByTag("foodsFragment") as? FoodsFragment


    //foodsFragment = FoodsFragment(id)
    val transaction = supportFragmentManager.beginTransaction()
    //
    if (foodsFragment == null) {
        foodsFragment = FoodsFragment(id)
        transaction.add(R.id.fcv_main, foodsFragment!!, "foodsFragment")
        transaction.hide(aiFragment)
        transaction.commit()
    }
    mainViewModel.changeMenuID(id)
    //transaction.commit()
    /*    supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_main, foodsFragment!!, "foodsFragment")
            .commit()
    } else {
        // 기존 프래그먼트의 데이터를 업데이트하는 로직 추가 (필요시)
        foodsFragment!!.updateData(id)
        supportFragmentManager.beginTransaction()
            .show(foodsFragment!!)
            .commit()
    }*/

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