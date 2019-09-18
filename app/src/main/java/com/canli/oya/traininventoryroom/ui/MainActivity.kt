package com.canli.oya.traininventoryroom.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ActivityMainBinding
import com.canli.oya.traininventoryroom.utils.ALL_TRAIN
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fm: FragmentManager

    private val trainListFragment by lazy { TrainListFragment() }
    private val brandListFragment by lazy { BrandListFragment() }
    private val categoryListFragment by lazy { CategoryListFragment() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.navigation.setOnNavigationItemSelectedListener(this)
        fm = supportFragmentManager
        fm.addOnBackStackChangedListener(this)

        //Bring the train fragment_list fragment at the launch of activity
        if (savedInstanceState == null) {
            fm.commit {
                setCustomAnimations(0, android.R.animator.fade_out)
                    .add(R.id.container, CategoryListFragment())
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val ft = fm.beginTransaction()
        val currentFrag = fm.findFragmentById(R.id.container)

        val id = item.itemId
        when (id){
            R.id.trains -> {
                if (currentFrag is TrainListFragment) {
                    currentFrag.scrollToTop()
                    return true
                }
                Timber.d("After return")
                val args = Bundle()
                args.putString(INTENT_REQUEST_CODE, ALL_TRAIN)
                trainListFragment.arguments = args
                ft.replace(R.id.container, trainListFragment)
            }
            R.id.brands -> {
                if (currentFrag is BrandListFragment) {
                    return true
                }
                ft.replace(R.id.container, brandListFragment)
            }
            R.id.categories -> {
                if (currentFrag is CategoryListFragment) {
                    return true
                }
                ft.replace(R.id.container, categoryListFragment)
            }
        }
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .commit()
        return true
    }

    private fun hideOrShowBottomNavigation(currentFrag: androidx.fragment.app.Fragment?) {
        if (currentFrag is AddTrainFragment) {
            binding.navigation.visibility = View.GONE
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            binding.navigation.visibility = View.VISIBLE
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun clearFocusAndHideKeyboard() {
        //This is for closing soft keyboard if user navigates to another fragment while keyboard was open
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = this.currentFocus
       focusedView?.run {
           clearFocus()
           imm.hideSoftInputFromWindow(this.windowToken, 0)
       }
    }

    private fun setMenuItemChecked(currentFrag: Fragment?) {
        /*If user navigates with back button, active menu item doesn't adapt itself.
        We need to set it checked programmatically.*/
        val itemNo = when (currentFrag) {
            is CategoryListFragment -> 0
            is BrandListFragment -> 1
            else -> 2
        }
        binding.navigation.menu.getItem(itemNo).isChecked = true
    }

    override fun onBackPressed() {
        val currentFrag = supportFragmentManager.findFragmentById(R.id.container)
        if (currentFrag is AddTrainFragment) {
            currentFrag.onBackClicked()
        } else {
            super.onBackPressed()
        }
    }

    override fun onBackStackChanged() {
        clearFocusAndHideKeyboard()
        val currentFrag = fm.findFragmentById(R.id.container)
        setMenuItemChecked(currentFrag)
        hideOrShowBottomNavigation(currentFrag)
    }
}
