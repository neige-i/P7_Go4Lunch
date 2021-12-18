package com.neige_i.go4lunch.domain.settings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.neige_i.go4lunch.data.preferences.PreferencesRepository;

import org.junit.Test;

public class HandleSettingsPreferencesUseCaseImplTest {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final PreferencesRepository preferencesRepositoryMock = mock(PreferencesRepository.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final HandleSettingsPreferencesUseCase handleSettingsPreferencesUseCase =
        new HandleSettingsPreferencesUseCaseImpl(preferencesRepositoryMock);

    @Test
    public void returnTrue_when_getMiddayNotification_withNotificationEnabled() {
        // GIVEN
        doReturn(true).when(preferencesRepositoryMock).getMiddayNotificationEnabled();

        // WHEN
        final boolean isEnabled = handleSettingsPreferencesUseCase.getMiddayNotification();

        // THEN
        assertTrue(isEnabled);
    }

    @Test
    public void returnFalse_when_getMiddayNotification_withNotificationDisabled() {
        // GIVEN
        doReturn(false).when(preferencesRepositoryMock).getMiddayNotificationEnabled();

        // WHEN
        final boolean isEnabled = handleSettingsPreferencesUseCase.getMiddayNotification();

        // THEN
        assertFalse(isEnabled);
    }

    @Test
    public void enableMiddayNotification_when_setMiddayNotification_withNotificationEnabled() {
        // WHEN
        handleSettingsPreferencesUseCase.setMiddayNotification(true);

        // THEN
        verify(preferencesRepositoryMock).setMiddayNotificationEnabled(true);
        verifyNoMoreInteractions(preferencesRepositoryMock);
    }

    @Test
    public void disableMiddayNotification_when_setMiddayNotification_withNotificationDisabled() {
        // WHEN
        handleSettingsPreferencesUseCase.setMiddayNotification(false);

        // THEN
        verify(preferencesRepositoryMock).setMiddayNotificationEnabled(false);
        verifyNoMoreInteractions(preferencesRepositoryMock);
    }
}