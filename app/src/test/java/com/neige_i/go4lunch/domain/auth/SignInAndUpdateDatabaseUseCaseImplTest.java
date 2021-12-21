package com.neige_i.go4lunch.domain.auth;

import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.neige_i.go4lunch.repository.firestore.FirestoreRepository;
import com.neige_i.go4lunch.repository.firestore.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

@SuppressWarnings("unchecked")
public class SignInAndUpdateDatabaseUseCaseImplTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
    private final FirestoreRepository firestoreRepositoryMock = mock(FirestoreRepository.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private SignInAndUpdateDatabaseUseCase signInAndUpdateDatabaseUseCase;

    // ----------------------------------- OTHER MOCKED OBJECTS ------------------------------------

    private final Task<AuthResult> authResultTaskMock = mock(Task.class);
    private final FirebaseUser firebaseAuthUserMock = mock(FirebaseUser.class);
    private final Uri uriMock = mock(Uri.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<User> firestoreUserMutableLiveData = new MutableLiveData<>();

    // ------------------------------------- ARGUMENT CAPTORS --------------------------------------

    private final ArgumentCaptor<OnSuccessListener<AuthResult>> onSuccessListenerCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
    private final ArgumentCaptor<OnFailureListener> onFailureListenerCaptor = ArgumentCaptor.forClass(OnFailureListener.class);

    // ------------------------------------------- CONST -------------------------------------------

    private static final String USER_ID = "user ID";
    private static final String USER_EMAIL = "user email";
    private static final String USER_NAME = "user name";
    private static final String USER_PHOTO = "user photo URL";

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(authResultTaskMock).when(firebaseAuthMock).signInWithCredential(any());
        doReturn(authResultTaskMock).when(authResultTaskMock).addOnSuccessListener(any());

        doReturn(firestoreUserMutableLiveData).when(firestoreRepositoryMock).getUser(any());

        doReturn(firebaseAuthUserMock).when(firebaseAuthMock).getCurrentUser();
        doReturn(USER_ID).when(firebaseAuthUserMock).getUid();
        doReturn(USER_EMAIL).when(firebaseAuthUserMock).getEmail();
        doReturn(USER_NAME).when(firebaseAuthUserMock).getDisplayName();
        doReturn(uriMock).when(firebaseAuthUserMock).getPhotoUrl();
        doReturn(USER_PHOTO).when(uriMock).toString();

        // Init UseCase
        signInAndUpdateDatabaseUseCase = new SignInAndUpdateDatabaseUseCaseImpl(
            firebaseAuthMock,
            firestoreRepositoryMock
        );
    }

    // ----------------------------------- SIGN-IN RESULT TESTS ------------------------------------

    @Test
    public void returnSuccess_when_signIn_withSuccess() {
        // GIVEN
        firestoreUserMutableLiveData.setValue(null); // Arbitrary value
        final SignInResult[] actualSignInResult = new SignInResult[1];

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
            actualSignInResult[0] = signInResult;
        });

        // Capture OnSuccessListener
        verify(authResultTaskMock).addOnSuccessListener(onSuccessListenerCaptor.capture());
        onSuccessListenerCaptor.getValue().onSuccess(mock(AuthResult.class));

        // THEN
        assertEquals(new SignInResult.Success(), actualSignInResult[0]);
    }

    @Test
    public void returnFailure_when_signIn_with_networkException() {
        // GIVEN
        final SignInResult[] actualSignInResult = new SignInResult[1];

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
            actualSignInResult[0] = signInResult;
        });

        // Capture OnFailureListener
        verify(authResultTaskMock).addOnFailureListener(onFailureListenerCaptor.capture());
        onFailureListenerCaptor.getValue().onFailure(mock(FirebaseNetworkException.class));

        // THEN
        assertEquals(
            new SignInResult.Failure(mock(FirebaseNetworkException.class)),
            actualSignInResult[0]
        );
    }

    @Test
    public void returnFailure_when_signIn_with_invalidUserException() {
        // GIVEN
        final SignInResult[] actualSignInResult = new SignInResult[1];

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
            actualSignInResult[0] = signInResult;
        });

        // Capture OnFailureListener
        verify(authResultTaskMock).addOnFailureListener(onFailureListenerCaptor.capture());
        onFailureListenerCaptor.getValue().onFailure(mock(FirebaseAuthInvalidUserException.class));

        // THEN
        assertEquals(
            new SignInResult.Failure(mock(FirebaseAuthInvalidUserException.class)),
            actualSignInResult[0]
        );
    }

    @Test
    public void returnFailure_when_signIn_with_invalidCredentialsException() {
        // GIVEN
        final SignInResult[] actualSignInResult = new SignInResult[1];

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
            actualSignInResult[0] = signInResult;
        });

        // Capture OnFailureListener
        verify(authResultTaskMock).addOnFailureListener(onFailureListenerCaptor.capture());
        onFailureListenerCaptor.getValue().onFailure(mock(FirebaseAuthInvalidCredentialsException.class));

        // THEN
        assertEquals(
            new SignInResult.Failure(mock(FirebaseAuthInvalidCredentialsException.class)),
            actualSignInResult[0]
        );
    }

    @Test
    public void returnFailure_when_signIn_with_userCollisionException() {
        // GIVEN
        final SignInResult[] actualSignInResult = new SignInResult[1];

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
            actualSignInResult[0] = signInResult;
        });

        // Capture OnFailureListener
        verify(authResultTaskMock).addOnFailureListener(onFailureListenerCaptor.capture());
        onFailureListenerCaptor.getValue().onFailure(mock(FirebaseAuthUserCollisionException.class));

        // THEN
        assertEquals(
            new SignInResult.Failure(mock(FirebaseAuthUserCollisionException.class)),
            actualSignInResult[0]
        );
    }

    @Test
    public void returnFailure_when_signIn_with_otherFirebaseException() {
        // GIVEN
        final SignInResult[] actualSignInResult = new SignInResult[1];

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
            actualSignInResult[0] = signInResult;
        });

        // Capture OnFailureListener
        verify(authResultTaskMock).addOnFailureListener(onFailureListenerCaptor.capture());
        onFailureListenerCaptor.getValue().onFailure(mock(FirebaseException.class));

        // THEN
        assertEquals(
            new SignInResult.Failure(mock(FirebaseException.class)),
            actualSignInResult[0]
        );
    }

    // ----------------------------------- UPDATE DATABASE TESTS -----------------------------------

    @Test
    public void addUserToFirestore_when_signInSuccessful_with_newUserWithPhoto() {
        // GIVEN
        firestoreUserMutableLiveData.setValue(null);

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
        });

        // Capture OnSuccessListener
        verify(authResultTaskMock).addOnSuccessListener(onSuccessListenerCaptor.capture());
        onSuccessListenerCaptor.getValue().onSuccess(mock(AuthResult.class));

        // THEN
        verify(firestoreRepositoryMock).getUser(any());
        verify(firestoreRepositoryMock).addUser(
            USER_ID,
            new User(
                USER_ID,
                USER_EMAIL,
                USER_NAME,
                USER_PHOTO,
                null,
                null
            )
        );
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void addUserToFirestore_when_signInSuccessful_with_newUserWithoutPhoto() {
        // GIVEN
        firestoreUserMutableLiveData.setValue(null);
        doReturn(null).when(firebaseAuthUserMock).getPhotoUrl();

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
        });

        // Capture OnSuccessListener
        verify(authResultTaskMock).addOnSuccessListener(onSuccessListenerCaptor.capture());
        onSuccessListenerCaptor.getValue().onSuccess(mock(AuthResult.class));

        // THEN
        verify(firestoreRepositoryMock).getUser(any());
        verify(firestoreRepositoryMock).addUser(
            USER_ID,
            new User(
                USER_ID,
                USER_EMAIL,
                USER_NAME,
                null, // No photo
                null,
                null
            )
        );
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void doNothing_when_signInSuccessful_with_newUserWithoutEmail() {
        // GIVEN
        firestoreUserMutableLiveData.setValue(null);
        doReturn(null).when(firebaseAuthUserMock).getEmail();

        // WHEN
        getValueForTesting(signInAndUpdateDatabaseUseCase.signInToFirebase(any()));

        // Capture OnSuccessListener
        verify(authResultTaskMock).addOnSuccessListener(onSuccessListenerCaptor.capture());
        onSuccessListenerCaptor.getValue().onSuccess(mock(AuthResult.class));

        // THEN
        verify(firestoreRepositoryMock).getUser(any());
        verify(firestoreRepositoryMock, never()).addUser(any(), any());
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void doNothing_when_signInSuccessful_with_newUserWithoutName() {
        // GIVEN
        firestoreUserMutableLiveData.setValue(null);
        doReturn(null).when(firebaseAuthUserMock).getDisplayName();

        // WHEN
        getValueForTesting(signInAndUpdateDatabaseUseCase.signInToFirebase(any()));

        // Capture OnSuccessListener
        verify(authResultTaskMock).addOnSuccessListener(onSuccessListenerCaptor.capture());
        onSuccessListenerCaptor.getValue().onSuccess(mock(AuthResult.class));

        // THEN
        verify(firestoreRepositoryMock).getUser(any());
        verify(firestoreRepositoryMock, never()).addUser(any(), any());
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }

    @Test
    public void doNothing_when_signInSuccessful_with_existingUser() {
        // GIVEN
        firestoreUserMutableLiveData.setValue(mock(User.class));

        // WHEN
        signInAndUpdateDatabaseUseCase.signInToFirebase(any()).observeForever(signInResult -> {
        });

        // Capture OnSuccessListener
        verify(authResultTaskMock).addOnSuccessListener(onSuccessListenerCaptor.capture());
        onSuccessListenerCaptor.getValue().onSuccess(mock(AuthResult.class));

        // THEN
        verify(firestoreRepositoryMock).getUser(any());
        verify(firestoreRepositoryMock, never()).addUser(any(), any());
        verifyNoMoreInteractions(firestoreRepositoryMock);
    }
}