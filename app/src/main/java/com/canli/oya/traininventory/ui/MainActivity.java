package com.canli.oya.traininventory.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.databinding.ActivityMainBinding;
import com.canli.oya.traininventory.utils.Constants;

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        FragmentManager.OnBackStackChangedListener, AddTrainFragment.UnsavedChangesListener {

    private ActivityMainBinding binding;
    private boolean thereAreUnsavedChanges;

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

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment currentFrag = fm.findFragmentById(R.id.container);

        int id = item.getItemId();
        switch (id) {
            case R.id.trains: {
                String tag = Constants.TAG_TRAINS;
                Fragment fragment = fm.findFragmentByTag(tag);
                if(currentFrag == fragment) {
                    ((TrainListFragment) fragment).scrollToTop();
                    break;
                }
                if (fragment == null) {
                    fragment = new TrainListFragment();
                    //ft.addToBackStack(tag);
                }
                ft.replace(R.id.container, fragment, tag)
                        .addToBackStack(tag);
                break;
            }
            case R.id.brands: {
                String tag = Constants.TAG_BRANDS;
                Fragment fragment = fm.findFragmentByTag(tag);
                if(currentFrag == fragment) {
                    break;
                }
                if (fragment == null) {
                    fragment = new BrandListFragment();
                    //ft.addToBackStack(tag);
                }
                ft.replace(R.id.container, fragment, tag)
                        .addToBackStack(tag);
                break;
            }
            case R.id.categories: {
                String tag = Constants.TAG_CATEGORIES;
                Fragment fragment = fm.findFragmentByTag(tag);
                if(currentFrag == fragment) {
                    break;
                }
                if (fragment == null) {
                    fragment = new CategoryListFragment();
                    //ft.addToBackStack(tag);
                }
                ft.replace(R.id.container, fragment, tag)
                        .addToBackStack(tag);
                break;
            }
        }
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .commit();
        return true;
    }

    @Override
    public void onBackStackChanged() {
        clearFocusAndHideKeyboard();
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.container);
        setMenuItemChecked(currentFrag);
        hideOrShowBottomNavigation(currentFrag);
    }

    private void hideOrShowBottomNavigation(Fragment currentFrag) {
        if(currentFrag instanceof AddTrainFragment){
            binding.navigation.setVisibility(GONE);
        } else {
            binding.navigation.setVisibility(View.VISIBLE);
        }
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

    private void setMenuItemChecked(Fragment currentFrag) {
        /*If user navigates with back button, active menu item doesn't adapt itself.
        We need to set it checked programmatically.*/
        if(currentFrag instanceof BrandListFragment){
            binding.navigation.getMenu().getItem(1).setChecked(true);
        } else if (currentFrag instanceof CategoryListFragment){
            binding.navigation.getMenu().getItem(2).setChecked(true);
        } else {
            binding.navigation.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.container);
        if(thereAreUnsavedChanges && currentFrag instanceof AddTrainFragment){
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showUnsavedChangesDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_warning);
        builder.setPositiveButton(getString(R.string.discard_changes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Changes will be discarded
                thereAreUnsavedChanges = false;
                onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void warnForUnsavedChanges(boolean shouldWarn) {
        thereAreUnsavedChanges = shouldWarn;
    }
}
