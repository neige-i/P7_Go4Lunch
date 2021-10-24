package com.neige_i.go4lunch.domain.list_workmate;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface GetAllWorkmatesUseCase {

    LiveData<List<Workmate>> get();
}
