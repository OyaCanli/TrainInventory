package com.canli.oya.traininventoryroom.ui

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ActivityMainBinding
import com.canli.oya.traininventoryroom.utils.ALL_TRAIN
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.utils.UNSAVED_CHANGES
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, AddTrainFragment.UnsavedChangesListener, androidx.fragment.app.FragmentManager.OnBackStackChangedListener {

    private lateinit var binding: ActivityMainBinding
    private var thereAreUnsavedChanges: Boolean = false
    private lateinit var fm: androidx.fragment.app.FragmentManager
    private var mTrainListFragment: TrainListFragment? = null
    private var mBrandListFragment: BrandListFragment? = null
    private var mCategoryListFragment: CategoryListFragment? = null

    private val trainListFragment: TrainListFragment
        get() {
            return mTrainListFragment ?: TrainListFragment().also{mTrainListFragment = it}
        }

    private val brandListFragment: BrandListFragment
        get() {
            return mBrandListFragment ?: BrandListFragment().also{mBrandListFragment = it}
        }

    private val categoryListFragment: CategoryListFragment
        get() {
            if (mCategoryListFragment == null) {
                mCategoryListFragment = CategoryListFragment()
            }
            return mCategoryListFragment ?: CategoryListFragment().also { mCategoryListFragment = it }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Set toolbar
        setSupportActionBar(binding.toolbar)

        binding.navigation.setOnNavigationItemSelectedListener(this)
        fm = supportFragmentManager
        fm.addOnBackStackChangedListener(this)

        //Bring the train list fragment at the launch of activity
        if (savedInstanceState == null) {
            fm.beginTransaction()
                    .setCustomAnimations(0, android.R.animator.fade_out)
                    .add(R.id.container, categoryListFragment)
                    .commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val ft = fm.beginTransaction()
        val currentFrag = fm.findFragmentById(R.id.container)

        val id = item.itemId
        switch@ when (id){
            R.id.trains -> {
                if (currentFrag is TrainListFragment) {
                    currentFrag.scrollToTop()
                    return@switch
                }
                val trainListFrag = trainListFragment
                val args = Bundle()
                args.putString(INTENT_REQUEST_CODE, ALL_TRAIN)
                trainListFrag.arguments = args
                ft.replace(R.id.container, trainListFrag)
            }
            R.id.brands -> {
                if (currentFrag is BrandListFragment) {
                    return@switch
                }
                ft.replace(R.id.container, brandListFragment)
            }
            R.id.categories -> {
                if (currentFrag is CategoryListFragment) {
                    return@switch
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
        val focusedView = this.currentFocus
        if (focusedView != null) {
            focusedView.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }

    fun setMenuItemChecked(currentFrag: androidx.fragment.app.Fragment?) {
        /*If user navigates with back button, active menu item doesn't adapt itself.
        We need to set it checked programmatically.*/
        if (currentFrag is BrandListFragment) {
            binding.navigation.menu.getItem(1).isChecked = true
        } else if (currentFrag is CategoryListFragment) {
            binding.navigation.menu.getItem(0).isChecked = true
        } else {
            binding.navigation.menu.getItem(2).isChecked = true
        }
    }

    override fun onBackPressed() {
        val currentFrag = supportFragmentManager.findFragmentById(R.id.container)
        if (thereAreUnsavedChanges && currentFrag is AddTrainFragment) {
            showUnsavedChangesDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showUnsavedChangesDialog() {
        //If user clicks back when there are unsaved changes in AddTrainFragment, warn user with a dialog.
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_warning)
        builder.setPositiveButton(getString(R.string.discard_changes)) { _, _ ->
            //Changes will be discarded
            thereAreUnsavedChanges = false
            onBackPressed()
        }
        builder.setNegativeButton(R.string.keep_editing) { dialog, id ->
            // User clicked the "Keep editing" button, so dismiss the dialog
            // and continue editing
            dialog?.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun warnForUnsavedChanges(shouldWarn: Boolean) {
        thereAreUnsavedChanges = shouldWarn
    }

    override fun onBackStackChanged() {
        clearFocusAndHideKeyboard()
        val currentFrag = fm.findFragmentById(R.id.container)
        setMenuItemChecked(currentFrag)
        hideOrShowBottomNavigation(currentFrag)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(UNSAVED_CHANGES, thereAreUnsavedChanges)
    }
}
