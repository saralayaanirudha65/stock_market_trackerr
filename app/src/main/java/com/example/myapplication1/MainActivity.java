package com.example.myapplication1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private TabAdapter tabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tabAdapter = new TabAdapter(this);
        viewPager.setAdapter(tabAdapter);

        // Disable swipe if you want only bottom nav
        viewPager.setUserInputEnabled(false);

        // Sync BottomNav clicks to ViewPager
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_search) {
                viewPager.setCurrentItem(0);
            } else if (id == R.id.nav_holdings) {
                viewPager.setCurrentItem(1);
            } else if (id == R.id.nav_watchlist) {
                viewPager.setCurrentItem(2);
            } else if (id == R.id.nav_news) { // Updated from nav_compare to nav_news
                viewPager.setCurrentItem(3);
            } else if (id == R.id.nav_settings) {
                viewPager.setCurrentItem(4);
            }
            return true;
        });

        // Sync ViewPager swipes to BottomNav
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });

        // Optional: top search icon click behavior
        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            Toast.makeText(this, "Search clicked!", Toast.LENGTH_SHORT).show();
            // Or open a search dialog/activity
        });
    }

    public void refreshHoldingsIfVisible() {
        // Check if the current fragment is HoldingsFragment
        if (viewPager.getCurrentItem() == 1) { // Assuming holdings is at position 1
            // Get the fragment and refresh it
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + viewPager.getCurrentItem());
            if (fragment instanceof HoldingsFragment) {
                ((HoldingsFragment) fragment).loadHoldings();
            }
        }
    }



}
