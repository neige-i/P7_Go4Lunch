package com.neige_i.go4lunch.view.auth;

import static com.neige_i.go4lunch.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.facebook.FacebookException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.neige_i.go4lunch.R;
import com.neige_i.go4lunch.domain.auth.SignInAndUpdateDatabaseUseCase;
import com.neige_i.go4lunch.domain.auth.SignInResult;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AuthViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final SignInAndUpdateDatabaseUseCase signInAndUpdateDatabaseUseCase = mock(SignInAndUpdateDatabaseUseCase.class);

    // ---------------------------------------- MOCK VALUES ----------------------------------------

    private final MutableLiveData<SignInResult> signInResultMutableLiveData = new MutableLiveData<>();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private AuthViewModel authViewModel;

    // ------------------------------------------- SETUP -------------------------------------------

    @Before
    public void setUp() {
        // Setup mocks
        doReturn(signInResultMutableLiveData).when(signInAndUpdateDatabaseUseCase).signInToFirebase(any());

        // Init ViewModel
        authViewModel = new AuthViewModel(signInAndUpdateDatabaseUseCase);
    }

    // ------------------------------------ LOGGING STATE TESTS ------------------------------------

    @Test
    public void startLogging_when_signInResultIsNotAvailableYet() throws InterruptedException {
        // GIVEN
        // No result available

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final boolean isLogging = getOrAwaitValue(authViewModel.getLoggingViewState());

        // THEN
        assertTrue(isLogging);
    }

    @Test
    public void stopLogging_when_signInResultFailed() throws InterruptedException {
        // GIVEN
        signInResultMutableLiveData.setValue(mock(SignInResult.Failure.class));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final boolean isLogging = getOrAwaitValue(authViewModel.getLoggingViewState());

        // THEN
        assertFalse(isLogging);
    }

    // ----------------------------------- SIGN-IN SUCCESS TESTS -----------------------------------

    @Test
    public void startHomeActivity_when_signInSucceeded() throws InterruptedException {
        // GIVEN
        signInResultMutableLiveData.setValue(new SignInResult.Success());

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final boolean isLogging = getOrAwaitValue(authViewModel.getLoggingViewState());
        final Void startHomeActivity = getOrAwaitValue(authViewModel.getStartHomeActivityEvent());

        // THEN
        assertTrue(isLogging); // Keep logging
        assertNull(startHomeActivity);
    }

    // ------------------------------- FIREBASE ERROR MESSAGE TESTS --------------------------------

    @Test
    public void returnFirebaseError_when_networkFailed() throws InterruptedException {
        // GIVEN
        authViewModel.getLoggingViewState().observeForever(aBoolean -> {
        });
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseNetworkException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.no_internet_error, errorMessageId);
    }

    @Test
    public void returnFirebaseError_when_accountIsDisabled() throws InterruptedException {
        // GIVEN
        authViewModel.getLoggingViewState().observeForever(aBoolean -> {
        });
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseAuthInvalidUserException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.invalid_user_error, errorMessageId);
    }

    @Test
    public void returnFirebaseError_when_wrongCredentials() throws InterruptedException {
        // GIVEN
        authViewModel.getLoggingViewState().observeForever(aBoolean -> {
        });
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseAuthInvalidCredentialsException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.invalid_credentials_error, errorMessageId);
    }

    @Test
    public void returnFirebaseError_when_accountAlreadyExists() throws InterruptedException {
        // GIVEN
        authViewModel.getLoggingViewState().observeForever(aBoolean -> {
        });
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseAuthUserCollisionException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.user_collision_error, errorMessageId);
    }

    @Test
    public void returnFirebaseError_when_elseFailed() throws InterruptedException {
        // GIVEN
        authViewModel.getLoggingViewState().observeForever(aBoolean -> {
        });
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.default_login_error, errorMessageId);
    }

    // -------------------------------- GOOGLE ERROR MESSAGE TESTS ---------------------------------

    @Test
    public void returnGoogleError_when_networkFailed() throws InterruptedException {
        // GIVEN
        authViewModel.handleGoogleSignInError(new ApiException(new Status(
            CommonStatusCodes.NETWORK_ERROR
        )));

        // WHEN
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.no_internet_error, errorMessageId);
    }

    @Test
    public void returnGoogleError_when_accountIsInvalid() throws InterruptedException {
        // GIVEN
        authViewModel.handleGoogleSignInError(new ApiException(new Status(
            CommonStatusCodes.INVALID_ACCOUNT
        )));

        // WHEN
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.invalid_account_error, errorMessageId);
    }

    @Test
    public void returnGoogleError_when_timedOut() throws InterruptedException {
        // GIVEN
        authViewModel.handleGoogleSignInError(new ApiException(new Status(
            CommonStatusCodes.TIMEOUT
        )));

        // WHEN
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.timeout_error, errorMessageId);
    }

    @Test
    public void returnGoogleError_when_elseFailed() throws InterruptedException {
        // GIVEN
        authViewModel.handleGoogleSignInError(new ApiException(new Status(
            -1
        )));

        // WHEN
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.default_login_error, errorMessageId);
    }

    // ------------------------------- FACEBOOK ERROR MESSAGE TESTS --------------------------------

    @Test
    public void returnFacebookError_when_internetIsDisconnected() throws InterruptedException {
        // GIVEN
        authViewModel.handleFacebookSignInError(new FacebookException(
            "net::ERR_INTERNET_DISCONNECTED"
        ));

        // WHEN
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.no_internet_error, errorMessageId);
    }

    @Test
    public void returnFacebookError_when_elseFailed() throws InterruptedException {
        // GIVEN
        authViewModel.handleFacebookSignInError(new FacebookException(
            ""
        ));

        // WHEN
        final int errorMessageId = getOrAwaitValue(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.default_login_error, errorMessageId);
    }
}