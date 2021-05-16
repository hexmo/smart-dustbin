package com.example.smartdustbin;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.smartdustbin.userManagement.ProfileFragment;
import com.example.smartdustbin.wasteManagement.AddDeviceFragment;
import com.example.smartdustbin.wasteManagement.DashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_frame_layout, new DashboardFragment()).commit();

    }

    //Switch fragment according to selected navigation menu
    final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment selectedFragment = null;

            switch (menuItem.getItemId()) {

                case R.id.homeFragment:
                    selectedFragment = new DashboardFragment();
                    break;

                case R.id.addDeviceFragment:
                    selectedFragment = new AddDeviceFragment();
                    break;

                case R.id.profileFragment:
                    selectedFragment = new ProfileFragment();
                    break;

            }
            //display fragment code

            getSupportFragmentManager().beginTransaction().replace(R.id.nav_frame_layout, selectedFragment).commit();
            return true;
        }
    };


}