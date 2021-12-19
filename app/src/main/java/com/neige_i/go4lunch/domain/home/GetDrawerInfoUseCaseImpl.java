package com.neige_i.go4lunch.domain.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.repository.firestore.FirestoreRepository;
import com.neige_i.go4lunch.repository.firestore.User;
import com.neige_i.go4lunch.domain.WorkmatesDelegate;

import javax.inject.Inject;

public class GetDrawerInfoUseCaseImpl implements GetDrawerInfoUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final WorkmatesDelegate workmatesDelegate;

    @Inject
    public GetDrawerInfoUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull WorkmatesDelegate workmatesDelegate
    ) {
        this.firestoreRepository = firestoreRepository;
        this.firebaseAuth = firebaseAuth;
        this.workmatesDelegate = workmatesDelegate;
    }

    @NonNull
    @Override
    public LiveData<DrawerInfo> get() {
        if (firebaseAuth.getCurrentUser() == null) {
            return new MutableLiveData<>();
        }

        return Transformations.map(
            firestoreRepository.getUser(firebaseAuth.getCurrentUser().getUid()), user -> {
                final User.SelectedRestaurant selectedRestaurant = user.getSelectedRestaurant();

                final String selectedRestaurantId;
                if (selectedRestaurant != null && workmatesDelegate.isToday(selectedRestaurant.getDate())) {
                    selectedRestaurantId = selectedRestaurant.getId();
                } else {
                    selectedRestaurantId = null;
                }

                return new DrawerInfo(
                    user.getPhotoUrl(),
                    user.getName(),
                    user.getEmail(),
                    selectedRestaurantId
                );
            }
        );
    }
}
