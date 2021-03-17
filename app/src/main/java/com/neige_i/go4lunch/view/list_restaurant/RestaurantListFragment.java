package com.neige_i.go4lunch.view.list_restaurant;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.util.OnDetailsQueriedCallback;
import com.neige_i.go4lunch.view.util.ViewModelFactory;

public class RestaurantListFragment extends Fragment {

    private OnDetailsQueriedCallback onDetailsQueriedCallback;

    public static RestaurantListFragment newInstance() {
        return new RestaurantListFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDetailsQueriedCallback = (OnDetailsQueriedCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RestaurantAdapter restaurantAdapter = new RestaurantAdapter(
            placeId -> onDetailsQueriedCallback.onDetailsQueried(placeId)
        );
        ((RecyclerView) requireView().findViewById(R.id.recyclerview)).setAdapter(restaurantAdapter);

        new ViewModelProvider(this, ViewModelFactory.getInstance())
            .get(RestaurantListViewModel.class)
            .getViewState()
            .observe(getViewLifecycleOwner(), restaurantAdapter::submitList);
    }
}
