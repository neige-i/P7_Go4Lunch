package com.neige_i.go4lunch.view.auth;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
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
    public void setLoggingTrue_when_signIn_with_noSignInResult() {
        // GIVEN (No result available)

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final boolean isLogging = getValueForTesting(authViewModel.getLoggingViewState());

        // THEN
        assertTrue(isLogging);
    }

    @Test
    public void setLoggingFalse_when_signIn_with_failureSignInResult() {
        // GIVEN
        signInResultMutableLiveData.setValue(mock(SignInResult.Failure.class));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final boolean isLogging = getValueForTesting(authViewModel.getLoggingViewState());

        // THEN
        assertFalse(isLogging);
    }

    @Test
    public void doNothing_when_signIn_with_wrongSignInResult() {
        // GIVEN
        signInResultMutableLiveData.setValue(new SignInResult() {
        });

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final boolean isLogging = getValueForTesting(authViewModel.getLoggingViewState());
        final int startHomeActivityTrigger = getLiveDataTriggerCount(authViewModel.getStartHomeActivityEvent());
        final int showErrorTrigger = getLiveDataTriggerCount(authViewModel.getShowErrorEvent());

        // THEN
        assertTrue(isLogging); // Keep logging
        assertEquals(0, startHomeActivityTrigger); // Never called
        assertEquals(0, showErrorTrigger); // Never called
    }

    // ----------------------------------- SIGN-IN SUCCESS TESTS -----------------------------------

    @Test
    public void startHomeActivity_when_signIn_with_successSignInResult() {
        // GIVEN
        signInResultMutableLiveData.setValue(new SignInResult.Success());

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final boolean isLogging = getValueForTesting(authViewModel.getLoggingViewState());
        final Void startHomeActivity = getValueForTesting(authViewModel.getStartHomeActivityEvent());

        // THEN
        assertTrue(isLogging); // Keep logging
        assertNull(startHomeActivity);
    }

    // ------------------------------- FIREBASE ERROR MESSAGE TESTS --------------------------------

    @Test
    public void returnInternetError_when_signIn_with_firebaseNetworkException() {
        // GIVEN
        getValueForTesting(authViewModel.getLoggingViewState());
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseNetworkException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.no_internet_error, errorMessageId);
    }

    @Test
    public void returnInvalidUserError_when_signIn_with_firebaseInvalidUserException() {
        // GIVEN
        getValueForTesting(authViewModel.getLoggingViewState());
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseAuthInvalidUserException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.invalid_user_error, errorMessageId);
    }

    @Test
    public void returnInvalidCredentialsError_when_signIn_with_firebaseInvalidCredentialsException() {
        // GIVEN
        getValueForTesting(authViewModel.getLoggingViewState());
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseAuthInvalidCredentialsException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.invalid_credentials_error, errorMessageId);
    }

    @Test
    public void returnUserCollisionError_when_signIn_with_firebaseUserCollisionException() {
        // GIVEN
        getValueForTesting(authViewModel.getLoggingViewState());
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseAuthUserCollisionException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.user_collision_error, errorMessageId);
    }

    @Test
    public void returnDefaultError_when_signIn_with_firebaseOtherFailureException() {
        // GIVEN
        getValueForTesting(authViewModel.getLoggingViewState());
        signInResultMutableLiveData.setValue(new SignInResult.Failure(mock(
            FirebaseException.class
        )));

        // WHEN
        authViewModel.signInToFirebase(mock(AuthCredential.class));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.default_login_error, errorMessageId);
    }

    // -------------------------------- GOOGLE ERROR MESSAGE TESTS ---------------------------------

    @Test
    public void returnInternetError_when_handleGoogleNetworkException() {
        // WHEN
        authViewModel.handleGoogleSignInError(new ApiException(new Status(
            CommonStatusCodes.NETWORK_ERROR
        )));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.no_internet_error, errorMessageId);
    }

    @Test
    public void returnInvalidAccountError_when_handleGoogleInvalidAccountException() {
        // WHEN
        authViewModel.handleGoogleSignInError(new ApiException(new Status(
            CommonStatusCodes.INVALID_ACCOUNT
        )));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.invalid_account_error, errorMessageId);
    }

    @Test
    public void returnTimeoutError_when_handleGoogleTimeoutException() {
        // WHEN
        authViewModel.handleGoogleSignInError(new ApiException(new Status(
            CommonStatusCodes.TIMEOUT
        )));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.timeout_error, errorMessageId);
    }

    @Test
    public void returnDefaultError_when_handleOtherGoogleException() {
        // WHEN
        authViewModel.handleGoogleSignInError(new ApiException(new Status(
            -1
        )));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.default_login_error, errorMessageId);
    }

    // ------------------------------- FACEBOOK ERROR MESSAGE TESTS --------------------------------

    @Test
    public void returnInternetError_when_handleFacebookInternetException() {
        // WHEN
        authViewModel.handleFacebookSignInError(new FacebookException(
            "net::ERR_INTERNET_DISCONNECTED"
        ));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.no_internet_error, errorMessageId);
    }

    @Test
    public void returnDefaultError_when_handleOtherFacebookException() {
        // WHEN
        authViewModel.handleFacebookSignInError(new FacebookException(
            ""
        ));
        final int errorMessageId = getValueForTesting(authViewModel.getShowErrorEvent());

        // THEN
        assertEquals(R.string.default_login_error, errorMessageId);
    }
}