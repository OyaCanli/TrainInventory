package com.canli.oya.traininventory.ui;

import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.canli.oya.traininventory.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private FloatingActionButton fab;

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

        fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(this);

        if(savedInstanceState == null) {
            TrainListFragment trainListFrag = new TrainListFragment();
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
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, trainListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.see_brands:{

                break;
            }
            case R.id.see_categories:{

                break;
            }
            case R.id.add_train:{
                AddTrainFragment addTrainFrag = new AddTrainFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addTrainFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_brand:{
                AddBrandFragment addBrandFrag = new AddBrandFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addBrandFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_category:{

                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_main){
            AddTrainFragment addTrainFrag = new AddTrainFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, addTrainFrag)
                    .addToBackStack(null)
                    .commit();
            fab.setVisibility(View.GONE);
        }
    }
}
