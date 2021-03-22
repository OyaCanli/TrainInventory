package com.canli.oya.traininventoryroom.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ActivityMainBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.canli.oya.traininventoryroom.ui.exportToExcel.ExportingToExcelDialog
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragment
import com.canli.oya.traininventoryroom.utils.shortToast
import com.google.android.material.navigation.NavigationView
import timber.log.Timber


class MainActivity : AppCompatActivity(),
    FragmentManager.OnBackStackChangedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fm: FragmentManager

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var navController: NavController

    private val appBarConfiguration = AppBarConfiguration(
        topLevelDestinationIds = setOf (
            R.id.categoryListFragment,
            R.id.brandListFragment,
            R.id.trainListFragment
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ComponentProvider.getInstance(application).daggerComponent.inject(this)

        //Set toolbar
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.navigationDrawer.setupWithNavController(navController)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)

        // Setting Navigation Controller with the BottomNavigationView
        binding.navigation.setupWithNavController(navController)


        //Set navigation drawer
        val drawer = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.setToolbarNavigationClickListener { onBackPressed() }

        drawer.addDrawerListener(toggle)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toggle.syncState()

        fm = supportFragmentManager
        fm.addOnBackStackChangedListener(this)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun toggleBottomNavigation(shouldShow: Boolean) {
        binding.navigation.visibility = if (shouldShow) View.VISIBLE else View.GONE
        toggle.isDrawerIndicatorEnabled = shouldShow
        supportActionBar?.setDisplayHomeAsUpEnabled(!shouldShow)
        toggle.syncState()
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
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val currentFrag = fm.findFragmentById(R.id.container)
            if (currentFrag is AddTrainFragment) {
                currentFrag.onBackClicked()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onBackStackChanged() {
        Timber.d("onBackStackChanged is called")
        clearFocusAndHideKeyboard()
        val currentFrag = fm.findFragmentById(R.id.container)
        setMenuItemChecked(currentFrag)
        //Hide bottom navigation in AddTrain and TrainDetails fragments
        val shouldShowBottomNavigation =
            !(currentFrag is AddTrainFragment || currentFrag is TrainDetailsFragment)
        toggleBottomNavigation(shouldShowBottomNavigation)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Called when you request permission to read and write to external storage
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, go ahead
                    //navigator.launchExportToExcelFragment()
                } else {
                    // If you do not get permission, show a Toast
                    shortToast(R.string.permission_denied)
                }
            }
        }
    }

    companion object {
        const val REQUEST_STORAGE_PERMISSION = 1
    }
}
