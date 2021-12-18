package com.neige_i.go4lunch.view.settings;

import static com.neige_i.go4lunch.LiveDataTestUtils.getLiveDataTriggerCount;
import static com.neige_i.go4lunch.LiveDataTestUtils.getValueForTesting;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.go4lunch.domain.settings.HandleSettingsPreferencesUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SettingsViewModelTest {

    // ----------------------------------------- TEST RULE -----------------------------------------

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final HandleSettingsPreferencesUseCase handleSettingsPreferencesUseCaseMock =
        mock(HandleSettingsPreferencesUseCase.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private SettingsViewModel settingsViewModel;

    @Before
    public void setUp() {
        doReturn(true).when(handleSettingsPreferencesUseCaseMock).getMiddayNotification();

        settingsViewModel = new SettingsViewModel(handleSettingsPreferencesUseCaseMock);
    }

    @Test
    public void returnTrue_when_initViewModel_with_enabledMiddayNotification() {
        // GIVEN
        doReturn(true).when(handleSettingsPreferencesUseCaseMock).getMiddayNotification();

        // WHEN
        final boolean isMiddayNotificationEnabled = getValueForTesting(settingsViewModel.getViewState());

        // THEN
        assertTrue(isMiddayNotificationEnabled);
    }

    @Test
    public void returnFalse_when_initViewModel_with_disabledMiddayNotification() {
        // GIVEN
        doReturn(false).when(handleSettingsPreferencesUseCaseMock).getMiddayNotification();
        settingsViewModel = new SettingsViewModel(handleSettingsPreferencesUseCaseMock);

        // WHEN
        final boolean isMiddayNotificationEnabled = getValueForTesting(settingsViewModel.getViewState());

        // THEN
        assertFalse(isMiddayNotificationEnabled);
    }

    @Test
    public void enableMiddayNotification_when_checkSwitch_with_enableState() {
        // WHEN
        settingsViewModel.onNotificationSwitchCheckChanged(true);

        // THEN
        verify(handleSettingsPreferencesUseCaseMock).getMiddayNotification();
        verify(handleSettingsPreferencesUseCaseMock).setMiddayNotification(true);
        verifyNoMoreInteractions(handleSettingsPreferencesUseCaseMock);
    }

    @Test
    public void disableMiddayNotification_when_checkSwitch_with_disableState() {
        // WHEN
        settingsViewModel.onNotificationSwitchCheckChanged(false);

        // THEN
        verify(handleSettingsPreferencesUseCaseMock).getMiddayNotification();
        verify(handleSettingsPreferencesUseCaseMock).setMiddayNotification(false);
        verifyNoMoreInteractions(handleSettingsPreferencesUseCaseMock);
    }

    @Test
    public void closeActivity_when_clickOnUpButton() {
        // WHEN
        settingsViewModel.onOptionsItemSelected(android.R.id.home);
        final int closeActivityTrigger = getLiveDataTriggerCount(settingsViewModel.getCloseActivityEvent());

        // THEN
        assertEquals(1, closeActivityTrigger);
    }

    @Test
    public void doNothing_when_clickOnOtherButton() {
        // WHEN
        settingsViewModel.onOptionsItemSelected(-1);
        final int closeActivityTrigger = getLiveDataTriggerCount(settingsViewModel.getCloseActivityEvent());

        // THEN
        assertEquals(0, closeActivityTrigger);
    }
}