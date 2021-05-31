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
import com.neige_i.go4lunch.view.OnDetailsQueriedCallback;
import com.neige_i.go4lunch.view.ViewModelFactory;

public class WorkmateListFragment extends Fragment {

    // --------------------------------------- LOCAL FIELDS ----------------------------------------

    private OnDetailsQueriedCallback onDetailsQueriedCallback;

    // -------------------------------------- FACTORY METHODS --------------------------------------

    public static WorkmateListFragment newInstance() {
        return new WorkmateListFragment();
    }

    // ------------------------------------- LIFECYCLE METHODS -------------------------------------

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

        // Init ViewModel
        final WorkmateListViewModel viewModel =
            new ViewModelProvider(this, ViewModelFactory.getInstance()).get(WorkmateListViewModel.class);

        // Init binding
        final FragmentListBinding binding = FragmentListBinding.bind(view);

        // Setup UI
        final WorkmateAdapter adapter = new WorkmateAdapter(viewModel::onWorkmateItemClicked);
        binding.recyclerview.setAdapter(adapter);

        // Update UI when state is changed
        viewModel.getViewState().observe(getViewLifecycleOwner(), adapter::submitList);

        // Update UI when event is triggered
        viewModel.getTriggerCallbackEvent().observe(getViewLifecycleOwner(), placeId ->
            onDetailsQueriedCallback.onDetailsQueried(placeId));
    }
}
