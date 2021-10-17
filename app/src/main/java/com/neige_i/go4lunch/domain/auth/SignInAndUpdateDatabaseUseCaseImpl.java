package com.neige_i.go4lunch.domain.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;
import com.neige_i.go4lunch.data.firestore.User;

import javax.inject.Inject;

public class SignInAndUpdateDatabaseUseCaseImpl implements SignInAndUpdateDatabaseUseCase {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final FirestoreRepository firestoreRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    @Inject
    SignInAndUpdateDatabaseUseCaseImpl(
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull FirestoreRepository firestoreRepository
    ) {
        this.firebaseAuth = firebaseAuth;
        this.firestoreRepository = firestoreRepository;
    }

    // ------------------------------------- USE CASE METHODS --------------------------------------

    @NonNull
    @Override
    public LiveData<SignInResult> signInToFirebase(@NonNull AuthCredential authCredential) {
        final MediatorLiveData<SignInResult> signInResultMediatorLiveData = new MediatorLiveData<>();

        firebaseAuth.signInWithCredential(authCredential)
            .addOnSuccessListener(authResult -> {
                signInResultMediatorLiveData.setValue(new SignInResult.Success());
                addUserToFirestore(signInResultMediatorLiveData);
            })
            .addOnFailureListener(e -> {
                signInResultMediatorLiveData.setValue(new SignInResult.Failure(e));
            });

        return signInResultMediatorLiveData;
    }

    private void addUserToFirestore(@NonNull MediatorLiveData<SignInResult> mediatorLiveData) {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null; // Just successfully signed in

        final String userId = firebaseUser.getUid();

        mediatorLiveData.addSource(firestoreRepository.getUser(userId), user -> {
            // Add user to Firestore if it does not exist
            if (user == null) {
                final User userToAdd = new User(
                    firebaseUser.getEmail(),
                    firebaseUser.getDisplayName(),
                    firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                    null,
                    0,
                    null
                );
                firestoreRepository.addUser(userId, userToAdd);
            }
        });
    }
}
