package com.canli.oya.traininventoryroom.ui.main

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ActivityMainBinding
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.ui.Navigator
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navigator : Navigator

    private lateinit var fm : FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        (application as TrainApplication).appComponent.inject(this)

        binding.navigation.setOnNavigationItemSelectedListener(this)
        fm = supportFragmentManager
        fm.addOnBackStackChangedListener(this)

        navigator.fragmentManager = fm

        //Bring the category list fragment at first launch of activity
        if (savedInstanceState == null) {
            navigator.launchCategoryList()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.trains -> navigator.launchTrainList()
            R.id.brands -> navigator.launchBrandList()
            R.id.categories -> navigator.launchCategoryList()
        }
        return true
    }

    private fun hideOrShowBottomNavigation(currentFrag: Fragment?) {
        if (currentFrag is AddTrainFragment) {
            binding.navigation.visibility = View.GONE
            binding.navigationDecoration.visibility = View.GONE
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            binding.navigation.visibility = View.VISIBLE
            binding.navigationDecoration.visibility = View.VISIBLE
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
        binding.navigationDecoration.setSelected(itemNo)
    }

    override fun onBackPressed() {
        val currentFrag = fm.findFragmentById(R.id.container)
        if (currentFrag is AddTrainFragment) {
            currentFrag.onBackClicked()
        } else {
            super.onBackPressed()
        }
    }

    override fun onBackStackChanged() {
        Timber.d("onBackStackChanged is called")
        clearFocusAndHideKeyboard()
        val currentFrag = fm.findFragmentById(R.id.container)
        setMenuItemChecked(currentFrag)
        hideOrShowBottomNavigation(currentFrag)
    }
}
