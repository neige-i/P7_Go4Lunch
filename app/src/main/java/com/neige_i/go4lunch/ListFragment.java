package com.neige_i.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ListFragment extends Fragment {

    public static final String WHICH_LIST = "which list";
    public static final int RESTAURANT = 0;
    public static final int WORKMATE = 1;

    public static ListFragment newInstance(int whichList) {
        final ListFragment fragment = new ListFragment();
        final Bundle args = new Bundle();
        args.putInt(WHICH_LIST, whichList);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the appropriate RecyclerView adapter according to the argument
        //noinspection rawtypes
        final ListAdapter adapter;
        switch (getArguments().getInt(WHICH_LIST)) {
            case RESTAURANT:
                adapter = new RestaurantAdapter();
                break;
            case WORKMATE:
                adapter = new WorkmateAdapter();
                break;
            default:
                throw new IllegalArgumentException();
        }
        ((RecyclerView) requireView().findViewById(R.id.recyclerview)).setAdapter(adapter);
        Log.d("Neige", "ListFragment::onViewCreated: show "
            + ((RecyclerView) requireView().findViewById(R.id.recyclerview)).getAdapter().getClass().getSimpleName());
    }
}
