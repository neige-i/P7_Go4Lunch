package com.neige_i.go4lunch.domain.list_workmate;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetAllWorkmatesUseCaseImpl implements GetAllWorkmatesUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final Clock clock;

    @Inject
    public GetAllWorkmatesUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull Clock clock
    ) {
        this.firestoreRepository = firestoreRepository;
        this.clock = clock;
    }

    @Override
    public LiveData<List<Workmate>> get() {
        return Transformations.map(firestoreRepository.getAllUsers(), userList -> {
            final List<Workmate> workmates = new ArrayList<>();

            for (User user : userList) {
                if (user.getEmail() == null || user.getName() == null) {
                    continue;
                }

                if (user.getSelectedRestaurantId() == null ||
                    user.getSelectedRestaurantName() == null ||
                    user.getSelectedRestaurantDate() == null
                ) {
                    workmates.add(new Workmate.WithoutRestaurant(
                        user.getEmail(),
                        user.getName(),
                        user.getPhotoUrl()
                    ));
                } else {
                    final LocalDate selectedDate = LocalDate.parse(
                        user.getSelectedRestaurantDate(),
                        FirestoreRepository.DATE_FORMATTER
                    );

                    if (ChronoUnit.DAYS.between(selectedDate, LocalDate.now(clock)) != 0) {
                        workmates.add(new Workmate.WithoutRestaurant(
                            user.getEmail(),
                            user.getName(),
                            user.getPhotoUrl()
                        ));
                    } else {
                        workmates.add(new Workmate.WithRestaurant(
                            user.getEmail(),
                            user.getName(),
                            user.getPhotoUrl(),
                            user.getSelectedRestaurantId(),
                            user.getSelectedRestaurantName()
                        ));
                    }
                }

            }

            return workmates;
        });
    }
}
