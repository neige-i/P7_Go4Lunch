package com.neige_i.go4lunch.view.list_workmate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.databinding.FragmentListBinding;
import com.neige_i.go4lunch.view.ImageDelegate;
import com.neige_i.go4lunch.view.StartDetailActivityCallback;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkmateListFragment extends Fragment {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @Inject
    ImageDelegate imageDelegate;

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    private StartDetailActivityCallback startDetailActivityCallback;

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        startDetailActivityCallback = (StartDetailActivityCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init binding
        final FragmentListBinding binding = FragmentListBinding.bind(view);

        // Setup UI
        final WorkmateAdapter adapter = new WorkmateAdapter(imageDelegate, placeId -> {
            startDetailActivityCallback.showDetailedInfo(placeId);
        });
        binding.recyclerview.setAdapter(adapter);

        // Update UI when state is changed
        new ViewModelProvider(this).get(WorkmateListViewModel.class)
            .getViewState()
            .observe(getViewLifecycleOwner(), viewStates -> adapter.submitList(viewStates));
    }
}
