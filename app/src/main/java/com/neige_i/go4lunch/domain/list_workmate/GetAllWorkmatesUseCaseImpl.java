package com.neige_i.go4lunch.domain.list_workmate;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;
import com.neige_i.go4lunch.domain.WorkmatesDelegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetAllWorkmatesUseCaseImpl implements GetAllWorkmatesUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final WorkmatesDelegate workmatesDelegate;

    @Inject
    public GetAllWorkmatesUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull WorkmatesDelegate workmatesDelegate
    ) {
        this.firestoreRepository = firestoreRepository;
        this.firebaseAuth = firebaseAuth;
        this.workmatesDelegate = workmatesDelegate;
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
                final boolean isRestaurantSelected = user.getSelectedRestaurantId() != null &&
                    user.getSelectedRestaurantName() != null && // ASKME: different condition for single selected
                    workmatesDelegate.isSelected(user.getSelectedRestaurantDate());

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

                workmatesDelegate.moveToFirstPosition(workmates, workmate -> workmate.isCurrentUser());
            }

            return workmates;
        });
    }
}
