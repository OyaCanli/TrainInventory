package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.canli.oya.traininventory.viewmodel.MainViewModel;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        AddTrainFragment.UnsavedChangesListener {

    private ActivityMainBinding binding;
    private boolean thereAreUnsavedChanges;
    private static final String TAG = "MainActivity";
    private FragmentManager fm;
    private MainViewModel mViewModel;
    private TrainListFragment mTrainListFragment;
    private BrandListFragment mBrandListFragment;
    private CategoryListFragment mCategoryListFragment;
    private TrainDetailsFragment mTrainDetailsFragment;
    private AddTrainFragment mAddTrainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //Set toolbar
        setSupportActionBar(binding.toolbar);

        binding.navigation.setOnNavigationItemSelectedListener(this);
        fm = getSupportFragmentManager();

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        //Bring the train list fragment at the launch of activity
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(0, android.R.animator.fade_out)
                    .add(R.id.container, getCategoryListFragment())
                    .commit();
            fm.executePendingTransactions();
            mViewModel.setCurrentFrag(Constants.TAG_CATEGORYLIST);
            mViewModel.fragmentHistory.add(Constants.TAG_CATEGORYLIST);
        }

        mViewModel.getCurrentFrag().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String tag) {
                onActiveFragmentChanged(tag);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment currentFrag = fm.findFragmentById(R.id.container);
        String fragmentTag = null;

        int id = item.getItemId();
        switch (id) {
            case R.id.trains: {
                if (currentFrag == getTrainListFragment()) {
                    getTrainListFragment().scrollToTop();
                    break;
                }
                TrainListFragment trainListFrag = getTrainListFragment();
                Bundle args = new Bundle();
                args.putString(Constants.INTENT_REQUEST_CODE, Constants.ALL_TRAIN);
                trainListFrag.setArguments(args);
                ft.replace(R.id.container, trainListFrag);
                fragmentTag = Constants.TAG_TRAINLIST;
                break;
            }
            case R.id.brands: {
                if (currentFrag == getBrandListFragment()) {
                    break;
                }
                ft.replace(R.id.container, getBrandListFragment());
                fragmentTag = Constants.TAG_BRANDLIST;
                break;
            }
            case R.id.categories: {
                if (currentFrag ==  getCategoryListFragment()) {
                    break;
                }
                ft.replace(R.id.container,  getCategoryListFragment());
                fragmentTag = Constants.TAG_CATEGORYLIST;
                break;
            }
        }
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .commit();
        fm.executePendingTransactions();
        mViewModel.arrangeFragmentHistory(fragmentTag);
        mViewModel.setCurrentFrag(fragmentTag);
        return true;
    }

    private void goBackToPreviousFragment() {
        if (mViewModel.fragmentHistory.size() > 1) {
            mViewModel.fragmentHistory.removeLast();
            String fragmentType = mViewModel.fragmentHistory.getLast();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, getCorrespondingFragment(fragmentType))
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .commit();
            fm.executePendingTransactions();
            mViewModel.setCurrentFrag(fragmentType);
        } else {
            super.onBackPressed();
        }
    }

    private Fragment getCorrespondingFragment(String tag) {
        Fragment fragment;
        switch(tag){
            case Constants.TAG_TRAINLIST:{
                fragment = getTrainListFragment();
                break;
            }
            case Constants.TAG_BRANDLIST:{
                fragment = getBrandListFragment();
                break;
            }
            case Constants.CATEGORY_NAME:{
                fragment = getCategoryListFragment();
                break;
            }
            case Constants.TAG_TRAIN_DETAILS:{
                fragment = getTrainDetailsFragment();
                break;
            }
            case Constants.TAG_ADD_TRAIN:{
                fragment = getAddTrainFragment();
                break;
            }
            default:{
                fragment = getCategoryListFragment();
            }
        }
        return fragment;
    }

    private void onActiveFragmentChanged(String tag) {
        clearFocusAndHideKeyboard();
        Fragment currentFrag = getCorrespondingFragment(tag);
        setMenuItemChecked(currentFrag);
        hideOrShowBottomNavigation(currentFrag);
    }

    private void hideOrShowBottomNavigation(Fragment currentFrag) {
        if (currentFrag instanceof AddTrainFragment) {
            binding.navigation.setVisibility(View.GONE);
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

    public void setMenuItemChecked(Fragment currentFrag) {
        /*If user navigates with back button, active menu item doesn't adapt itself.
        We need to set it checked programmatically.*/
        if (currentFrag instanceof BrandListFragment) {
            binding.navigation.getMenu().getItem(1).setChecked(true);
        } else if (currentFrag instanceof CategoryListFragment) {
            binding.navigation.getMenu().getItem(0).setChecked(true);
        } else {
            binding.navigation.getMenu().getItem(2).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.container);
        if (thereAreUnsavedChanges && currentFrag instanceof AddTrainFragment) {
            showUnsavedChangesDialog();
        } else {
            goBackToPreviousFragment();
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

    public TrainListFragment getTrainListFragment() {
        if(mTrainListFragment == null){
            mTrainListFragment = new TrainListFragment();
        }
        return mTrainListFragment;
    }

    public BrandListFragment getBrandListFragment() {
        if (mBrandListFragment == null) {
            mBrandListFragment = new BrandListFragment();
        }
        return mBrandListFragment;
    }

    public CategoryListFragment getCategoryListFragment() {
        if (mCategoryListFragment == null) {
            mCategoryListFragment = new CategoryListFragment();
        }
        return mCategoryListFragment;
    }

    public TrainDetailsFragment getTrainDetailsFragment() {
        if (mTrainDetailsFragment == null) {
            mTrainDetailsFragment = new TrainDetailsFragment();
        }
        return mTrainDetailsFragment;
    }

    public AddTrainFragment getAddTrainFragment() {
        if (mAddTrainFragment == null) {
            mAddTrainFragment = new AddTrainFragment();
        }
        return mAddTrainFragment;
    }
}
