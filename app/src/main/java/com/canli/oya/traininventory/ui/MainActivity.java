package com.canli.oya.traininventory.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.transition.Slide;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.canli.oya.traininventory.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentManager.OnBackStackChangedListener{

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if(savedInstanceState == null) {
            TrainListFragment trainListFrag = new TrainListFragment();
            trainListFrag.setExitTransition(new Slide(Gravity.START));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, trainListFrag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.see_trains:{
                TrainListFragment trainListFrag = new TrainListFragment();
                trainListFrag.setEnterTransition(new Slide(Gravity.END));
                trainListFrag.setExitTransition(new Slide(Gravity.START));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, trainListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.see_brands:{
                BrandListFragment brandListFrag = new BrandListFragment();
                brandListFrag.setEnterTransition(new Slide(Gravity.END));
                brandListFrag.setExitTransition(new Slide(Gravity.START));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, brandListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_train:{
                AddTrainFragment addTrainFrag = new AddTrainFragment();
                addTrainFrag.setEnterTransition(new Slide(Gravity.END));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addTrainFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_category:{
                CategoryListFragment catListFrag = new CategoryListFragment();
                catListFrag.setEnterTransition(new Slide(Gravity.END));
                catListFrag.setExitTransition(new Slide(Gravity.START));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, catListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackStackChanged() {
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            focusedView.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }
}
