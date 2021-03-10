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
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ActivityMainBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragment
import com.canli.oya.traininventoryroom.utils.shortToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navigator: Navigator

    private lateinit var fm: FragmentManager

    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ComponentProvider.getInstance(application).daggerComponent.inject(this)

        //Set toolbar
        setSupportActionBar(binding.toolbar)

        //Set bottom navigation
        binding.navigation.setOnNavigationItemSelectedListener(this)

        //Set navigation drawer
        val drawer = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
                this, drawer, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.setToolbarNavigationClickListener { onBackPressed() }

        drawer.addDrawerListener(toggle)
        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toggle.syncState()

        fm = supportFragmentManager
        fm.addOnBackStackChangedListener(this)
        navigator.fragmentManager = fm

        //Bring the category list fragment at first launch of activity
        if (savedInstanceState == null) {
            navigator.launchCategoryList()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.trains -> navigator.launchTrainList()
            R.id.brands -> navigator.launchBrandList()
            R.id.categories -> navigator.launchCategoryList()
            R.id.action_export -> checkPermissionAndExport()
        }
        return true
    }

    private fun checkPermissionAndExport(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkWritePermission()
        } else {
            navigator.launchExportToExcelFragment()
        }
    }

    private fun checkWritePermission() {
        if (needsPermission()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        } else {
            navigator.launchExportToExcelFragment()
        }
    }

    //Check whether permission is already given or not
    private fun needsPermission() = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

    private fun hideOrShowBottomNavigation(currentFrag: Fragment?) {
        if (currentFrag is AddTrainFragment || currentFrag is TrainDetailsFragment) {
            binding.navigation.visibility = View.GONE
            toggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            binding.navigation.visibility = View.VISIBLE
            toggle.isDrawerIndicatorEnabled = true
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
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
        hideOrShowBottomNavigation(currentFrag)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Called when you request permission to read and write to external storage
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, go ahead
                    navigator.launchExportToExcelFragment()
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
