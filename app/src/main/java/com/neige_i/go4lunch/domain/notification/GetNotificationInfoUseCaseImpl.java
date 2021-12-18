package com.neige_i.go4lunch.domain.notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;
import com.neige_i.go4lunch.domain.WorkmatesDelegate;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class GetNotificationInfoUseCaseImpl implements GetNotificationInfoUseCase {

    @NonNull
    private final FirestoreRepository firestoreRepository;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final WorkmatesDelegate workmatesDelegate;

    @Inject
    GetNotificationInfoUseCaseImpl(
        @NonNull FirestoreRepository firestoreRepository,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull WorkmatesDelegate workmatesDelegate
    ) {
        this.firestoreRepository = firestoreRepository;
        this.firebaseAuth = firebaseAuth;
        this.workmatesDelegate = workmatesDelegate;
    }

    @Nullable
    @Override
    public NotificationInfo get() {
        if (firebaseAuth.getCurrentUser() == null) {
            return null;
        }

        final User currentUser = firestoreRepository.getUserByIdSync(firebaseAuth.getCurrentUser().getUid());

        if (currentUser == null) {
            return null;
        }

        final User.SelectedRestaurant selectedRestaurant = currentUser.getSelectedRestaurant();

        if (selectedRestaurant == null || !workmatesDelegate.isToday(selectedRestaurant.getDate())) {
            return null;
        }

        final List<User> workmates = firestoreRepository.getWorkmatesEatingAtSync(selectedRestaurant.getId());

        return new NotificationInfo(
            selectedRestaurant.getId(),
            selectedRestaurant.getName(),
            selectedRestaurant.getAddress(),
            workmates
                .stream()
                .filter(user -> !user.getEmail().equals(currentUser.getEmail()))
                .map(workmate -> workmate.getName())
                .collect(Collectors.toList())
        );
    }
}
