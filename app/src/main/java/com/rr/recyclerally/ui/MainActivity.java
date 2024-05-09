package com.rr.recyclerally.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.rr.recyclerally.R;
import com.rr.recyclerally.ui.fragments.AboutFragment;
import com.rr.recyclerally.ui.fragments.ChallengesFragment;
import com.rr.recyclerally.ui.fragments.HomeFragment;
import com.rr.recyclerally.ui.fragments.ItemsFragment;
import com.rr.recyclerally.ui.fragments.ProfileFragment;
import com.rr.recyclerally.ui.fragments.SettingsFragment;
import com.rr.recyclerally.ui.fragments.TutorialFragment;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;

    private Fragment currentFragment;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initComponents();

        if (savedInstanceState == null) {
            // este null cand am intrat prima data
            // la rotirea dispozitivului nu mai este null
            currentFragment = new HomeFragment();
            openFragment();
            navigationView.setCheckedItem(R.id.nav_home);
        }


    }

    private void initComponents() {
        configNavigation();

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(getAddListener());

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(getBottomNavigationItemSelectedListener());

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(getNavigationItemSelectedListener());

    }

    private NavigationBarView.OnItemSelectedListener getBottomNavigationItemSelectedListener() {
        return new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_home_is_selected, Toast.LENGTH_SHORT).show();
                    currentFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_items) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_items_are_loading, Toast.LENGTH_SHORT).show();
                    currentFragment = new ItemsFragment();
                } else if (item.getItemId() == R.id.nav_challenges) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_challenges_are_loading, Toast.LENGTH_SHORT).show();
                    currentFragment = new ChallengesFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_profile_is_selected, Toast.LENGTH_SHORT).show();
                    currentFragment = new ProfileFragment();
                }
                openFragment();
                return true;
            }
        };
    }

    private NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener() {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_home_is_selected, Toast.LENGTH_SHORT).show();
                    currentFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_about) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_about_is_selected, Toast.LENGTH_SHORT).show();
                    currentFragment = new AboutFragment();
                } else if (item.getItemId() == R.id.nav_tutorial) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_tutorial_is_selected, Toast.LENGTH_SHORT).show();
                    currentFragment = new TutorialFragment();
                } else if (item.getItemId() == R.id.nav_settings) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_settings_is_selected, Toast.LENGTH_SHORT).show();
                    currentFragment = new SettingsFragment();
                } else if (item.getItemId() == R.id.nav_logout) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_logging_out, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                }
                openFragment();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        };
    }

    private void configNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_navigation_drawer,
                R.string.close_navigation_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    private View.OnClickListener getAddListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    private void openFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame_container, currentFragment)
                .commit();
    }
}