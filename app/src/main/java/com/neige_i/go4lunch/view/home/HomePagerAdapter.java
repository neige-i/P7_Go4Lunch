package com.neige_i.go4lunch.view.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.neige_i.go4lunch.view.list_restaurant.RestaurantListFragment;
import com.neige_i.go4lunch.view.list_workmate.WorkmateListFragment;
import com.neige_i.go4lunch.view.map.MapFragment;

class HomePagerAdapter extends FragmentStateAdapter {

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    /**
     * Fragments to put in the {@code ViewPager}.
     */
    private static final Fragment[] FRAGMENTS_TO_DISPLAY = new Fragment[]{
        new MapFragment(),
        RestaurantListFragment.newInstance(),
        WorkmateListFragment.newInstance()
    };

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    // -------------------------------------- ADAPTER METHODS --------------------------------------

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return FRAGMENTS_TO_DISPLAY[position];
    }

    @Override
    public int getItemCount() {
        return FRAGMENTS_TO_DISPLAY.length;
    }
}
