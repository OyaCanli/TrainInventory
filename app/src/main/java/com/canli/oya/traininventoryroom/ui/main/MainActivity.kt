package com.canli.oya.traininventoryroom.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ActivityMainBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.utils.shortToast
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val appBarConfiguration = AppBarConfiguration(
        topLevelDestinationIds = setOf(
            R.id.trainListFragment,
            R.id.filterTrainFragment,
            R.id.categoryListFragment,
            R.id.brandListFragment
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

        setupActionBarWithNavController(this, navController, appBarConfiguration)
        binding.navigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id == R.id.addTrainFragment || destination.id == R.id.trainDetailsFragment) {
                binding.navigation.visibility = View.GONE
            } else {
                binding.navigation.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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
