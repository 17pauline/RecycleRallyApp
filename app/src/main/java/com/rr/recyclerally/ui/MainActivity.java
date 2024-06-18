package com.rr.recyclerally.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.rr.recyclerally.R;
import com.rr.recyclerally.database.UserSession;
import com.rr.recyclerally.model.user.AUser;
import com.rr.recyclerally.model.user.Recycler;
import com.rr.recyclerally.ui.fragments.AboutFragment;
import com.rr.recyclerally.ui.fragments.AdminItemsFragment;
import com.rr.recyclerally.ui.fragments.ChallengesFragment;
import com.rr.recyclerally.ui.fragments.HomeFragment;
import com.rr.recyclerally.ui.fragments.ItemsFragment;
import com.rr.recyclerally.ui.fragments.ProfileFragment;
import com.rr.recyclerally.ui.fragments.SettingsFragment;
import com.rr.recyclerally.ui.fragments.TutorialFragment;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private static final String MAIN_ACTIVITY_TAG = "MainActivity";
    private static final String PREFS_NAME = "RecycleRallyPrefs";

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

        initializeOpencv();
    }

    private void initializeOpencv() {
        // IMPORTANT
        if (!OpenCVLoader.initDebug()) {
            Log.e(MAIN_ACTIVITY_TAG, getString(R.string.log_opencv_initialization_failed));
            System.loadLibrary("opencv_java4");
        } else {
            Log.d(MAIN_ACTIVITY_TAG, getString(R.string.log_opencv_initialization_succeeded));
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
                    currentFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_items) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_items_are_loading, Toast.LENGTH_SHORT).show();
                    if (UserSession.getInstance().isRecycler()) {
                        currentFragment = new ItemsFragment();
                    } else {
                        currentFragment = new AdminItemsFragment();
                    }
                } else if (item.getItemId() == R.id.nav_challenges) {
                    Toast.makeText(getApplicationContext(),
                            R.string.toast_challenges_are_loading, Toast.LENGTH_SHORT).show();
                    currentFragment = new ChallengesFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
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
                    currentFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_about) {
                    currentFragment = new AboutFragment();
                } else if (item.getItemId() == R.id.nav_tutorial) {
                    currentFragment = new TutorialFragment();
                } else if (item.getItemId() == R.id.nav_settings) {
                    currentFragment = new SettingsFragment();
                } else if (item.getItemId() == R.id.nav_logout) {
                    handleLogout();
                }
                openFragment();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        };
    }

    private void handleLogout() {
        UserSession.getInstance().clear();
        FirebaseAuth.getInstance().signOut();

        clearSharedPreferences();

        Toast.makeText(getApplicationContext(),
                R.string.toast_logging_out, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
        finish();
    }

    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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
                AUser user = UserSession.getInstance().getUser();
                if (user instanceof Recycler) {
                    Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), AdminAddChallengeActivity.class);
                    startActivity(intent);
                }
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