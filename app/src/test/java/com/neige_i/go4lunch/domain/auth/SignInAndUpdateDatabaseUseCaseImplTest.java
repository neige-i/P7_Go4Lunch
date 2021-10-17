package com.neige_i.go4lunch.domain.auth;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.data.firestore.FirestoreRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SignInAndUpdateDatabaseUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);

    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);
    private final Uri uriMock = mock(Uri.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<FirebaseUser> firebaseUserMutableLiveData = new MutableLiveData<>();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private SignInAndUpdateDatabaseUseCase signInAndUpdateDatabaseUseCase;

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() throws Exception {
        // Setup mocks
        doReturn(Task.class).when(firebaseAuthMock).signInWithCredential(any());
        doReturn(firebaseUserMock).when(firebaseAuthMock).getCurrentUser();
        doReturn("user email").when(firebaseUserMock).getEmail();
        doReturn("user name").when(firebaseUserMock).getDisplayName();
        doReturn(uriMock).when(firebaseUserMock).getPhotoUrl();
        doReturn("photo url").when(uriMock).toString();

        doReturn(firebaseUserMutableLiveData).when(firestoreRepositoryMock).getUser("EXISTING_USER");
        doReturn(null).when(firestoreRepositoryMock).getUser("NEW_USER");

        // Init UseCase
        signInAndUpdateDatabaseUseCase = new SignInAndUpdateDatabaseUseCaseImpl(firebaseAuthMock, firestoreRepositoryMock);
    }

    // --------------------------------------- SIGN-IN TESTS ---------------------------------------

    @Test
    public void returnSuccess_when_signInSucceeds() throws InterruptedException {
        // GIVEN
        final AuthCredential goodAuthCredential = mock(AuthCredential.class);

        // WHEN
        final SignInResult signInResult = getOrAwaitValue(signInAndUpdateDatabaseUseCase.signInToFirebase(goodAuthCredential));

        // THEN
        assertEquals(new SignInResult.Success(), signInResult);
    }

    @Test
    public void returnFailure_when_signInFails() throws InterruptedException {
        // GIVEN
        final AuthCredential wrongAuthCredential = mock(AuthCredential.class);

        // WHEN
        final SignInResult signInResult = getOrAwaitValue(signInAndUpdateDatabaseUseCase.signInToFirebase(wrongAuthCredential));

        // THEN
        assertEquals(new SignInResult.Failure(new Exception()), signInResult);
    }

    @Test
    public void addFirestoreUser_when_databaseDoesNotContainIt() throws InterruptedException {
        // GIVEN
        final AuthCredential wrongAuthCredential = mock(AuthCredential.class);

        // WHEN
        final SignInResult signInResult = getOrAwaitValue(signInAndUpdateDatabaseUseCase.signInToFirebase(wrongAuthCredential));

        // THEN
        assertEquals(new SignInResult.Failure(new Exception()), signInResult);
    }
}