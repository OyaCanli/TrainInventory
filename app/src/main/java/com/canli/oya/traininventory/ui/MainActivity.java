package com.canli.oya.traininventory.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.databinding.ActivityMainBinding;
import com.canli.oya.traininventory.utils.Constants;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        FragmentManager.OnBackStackChangedListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //Set toolbar
        setSupportActionBar(binding.toolbar);

        binding.navigation.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        //Bring the train list fragment at the launch of activity
        if (savedInstanceState == null) {
            TrainListFragment trainListFrag = new TrainListFragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(0, android.R.animator.fade_out)
                    .add(R.id.container, trainListFrag)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.trains: {
                loadFragment(new TrainListFragment(), Constants.TAG_TRAINS);
                break;
            }
            case R.id.brands: {
                loadFragment(new BrandListFragment(), Constants.TAG_BRANDS);
                break;
            }
            case R.id.categories: {
                loadFragment(new CategoryListFragment(), Constants.TAG_CATEGORIES);
                break;
            }
        }
        return true;
    }

    private void loadFragment(Fragment newFrag, String tag) {
        //This method loads a new fragment, if there isn't already an instance of it.
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment currentFrag = fm.findFragmentById(R.id.container);
        if (currentFrag != null) {
            ft.detach(currentFrag);
        }

        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = newFrag;
            ft.replace(R.id.container, fragment, tag)
                    .addToBackStack(tag);
        } else {
            ft.attach(fragment);
        }

        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .commit();
    }


    @Override
    public void onBackStackChanged() {
        clearFocusAndHideKeyboard();
        setMenuItemChecked();
    }

    private void clearFocusAndHideKeyboard() {
        //This is for closing soft keyboard if user navigates to another fragment while keyboard was open
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            focusedView.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    private void setMenuItemChecked() {
        /*If user navigates with back button, active menu item doesn't adapt itself.
        We need to set it checked programmatically.*/
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.container);
        if(currentFrag instanceof BrandListFragment){
            binding.navigation.getMenu().getItem(1).setChecked(true);
        } else if (currentFrag instanceof CategoryListFragment){
            binding.navigation.getMenu().getItem(2).setChecked(true);
        } else {
            binding.navigation.getMenu().getItem(0).setChecked(true);
        }
    }
}
