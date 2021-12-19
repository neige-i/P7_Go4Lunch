package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

class HomePagerAdapter extends FragmentStateAdapter {

    @NonNull
    private final List<Fragment> fragmentsToDisplay;

    HomePagerAdapter(
        @NonNull FragmentActivity fragmentActivity,
        @NonNull List<Fragment> fragmentsToDisplay
    ) {
        super(fragmentActivity);
        this.fragmentsToDisplay = fragmentsToDisplay;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentsToDisplay.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentsToDisplay.size();
    }
}
