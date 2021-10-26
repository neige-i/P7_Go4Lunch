package com.neige_i.go4lunch.domain.list_workmate;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;
import com.neige_i.go4lunch.domain.MoveListItemDelegate;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetAllWorkmatesUseCaseImpl implements GetAllWorkmatesUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final Clock clock;
    @NonNull
    private final MoveListItemDelegate moveListItemDelegate;

    @Inject
    public GetAllWorkmatesUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull Clock clock,
        @NonNull MoveListItemDelegate moveListItemDelegate
    ) {
        this.firestoreRepository = firestoreRepository;
        this.firebaseAuth = firebaseAuth;
        this.clock = clock;
        this.moveListItemDelegate = moveListItemDelegate;
    }

    @Override
    public LiveData<List<Workmate>> get() {
        return Transformations.map(firestoreRepository.getAllUsers(), userList -> {
            final List<Workmate> workmates = new ArrayList<>();

            for (User user : userList) {
                if (user.getEmail() == null || user.getName() == null) {
                    continue;
                }

                final boolean isCurrentUser = firebaseAuth.getCurrentUser() != null &&
                    user.getEmail().equals(firebaseAuth.getCurrentUser().getEmail());

                // Setup if restaurant is selected
                final boolean isRestaurantSelected;
                if (user.getSelectedRestaurantId() == null ||
                    user.getSelectedRestaurantName() == null ||
                    user.getSelectedRestaurantDate() == null
                ) {
                    isRestaurantSelected = false;
                } else {
                    isRestaurantSelected = LocalDate
                        .parse(user.getSelectedRestaurantDate(), FirestoreRepository.DATE_FORMATTER)
                        .isEqual(LocalDate.now(clock));
                }

                if (isRestaurantSelected) {
                    workmates.add(new Workmate.WithRestaurant(
                        user.getEmail(),
                        user.getName(),
                        user.getPhotoUrl(),
                        isCurrentUser,
                        user.getSelectedRestaurantId(),
                        user.getSelectedRestaurantName()
                    ));
                } else {
                    workmates.add(new Workmate.WithoutRestaurant(
                        user.getEmail(),
                        user.getName(),
                        user.getPhotoUrl(),
                        isCurrentUser
                    ));
                }

                moveListItemDelegate.toFirstPosition(workmates, workmate -> workmate.isCurrentUser());
            }

            return workmates;
        });
    }
}
