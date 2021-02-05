package com.neige_i.go4lunch.view.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.view.OnDetailQueriedCallback;
import com.neige_i.go4lunch.view.ViewModelFactory;

public class ListFragment extends Fragment {

    public static final String WHICH_LIST = "which list";
    public static final int RESTAURANT = 0;
    public static final int WORKMATE = 1;

    private OnDetailQueriedCallback onDetailQueriedCallback;

    public static ListFragment newInstance(int whichList) {
        final ListFragment fragment = new ListFragment();
        final Bundle args = new Bundle();
        args.putInt(WHICH_LIST, whichList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDetailQueriedCallback = (OnDetailQueriedCallback) context;
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
                adapter = new RestaurantAdapter(placeId -> onDetailQueriedCallback.onDetailQueried(placeId));
                break;
            case WORKMATE:
                adapter = new WorkmateAdapter();
                break;
            default:
                throw new IllegalArgumentException();
        }
        ((RecyclerView) requireView().findViewById(R.id.recyclerview)).setAdapter(adapter);

        //noinspection unchecked
        new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListViewModel.class)
            .getViewState()
            .observe(getViewLifecycleOwner(), adapter::submitList);
    }
}
