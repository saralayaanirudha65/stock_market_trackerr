package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {
    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SearchFragment();
            case 1:
                return new HoldingsFragment();
            case 2:
                return new WatchlistFragment();
            case 3:
                return new NewsFragment();
            case 4:
                return new SettingsFragment();
            default:
                return new SearchFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
