package com.neige_i.go4lunch.data.preferences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;

public class PreferencesRepositoryImplTest {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final Context applicationContextMock = mock(Context.class);

    // ------------------------------------ OTHER MOCK OBJECTS -------------------------------------

    final SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
    private final SharedPreferences.Editor editorMock = mock(SharedPreferences.Editor.class);

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private PreferencesRepository preferencesRepository;

    @Before
    public void setUp() {
        doReturn(sharedPreferencesMock).when(applicationContextMock)
            .getSharedPreferences(PreferencesRepositoryImpl.FILE_PREFERENCES, Context.MODE_PRIVATE);
        doReturn(editorMock).when(sharedPreferencesMock).edit();
        doReturn(editorMock).when(editorMock).putBoolean(anyString(), anyBoolean());

        preferencesRepository = new PreferencesRepositoryImpl(applicationContextMock);
    }

    @Test
    public void returnTrue_when_getMiddayNotification_with_notificationEnabled() {
        // GIVEN
        doReturn(true).when(sharedPreferencesMock)
            .getBoolean(PreferencesRepositoryImpl.KEY_MIDDAY_NOTIFICATION, true);

        // WHEN
        final boolean middayNotificationEnabled = preferencesRepository.getMiddayNotificationEnabled();

        // THEN
        assertTrue(middayNotificationEnabled);
    }

    @Test
    public void returnFalse_when_getMiddayNotification_with_notificationDisabled() {
        // GIVEN
        doReturn(false).when(sharedPreferencesMock)
            .getBoolean(PreferencesRepositoryImpl.KEY_MIDDAY_NOTIFICATION, true);

        // WHEN
        final boolean middayNotificationEnabled = preferencesRepository.getMiddayNotificationEnabled();

        // THEN
        assertFalse(middayNotificationEnabled);
    }

    @Test
    public void setNotificationPreferenceToTrue_when_setMiddayNotification_with_notificationEnabled() {
        // WHEN
        preferencesRepository.setMiddayNotificationEnabled(true);

        // THEN
        verify(sharedPreferencesMock).edit();
        verify(editorMock).putBoolean(PreferencesRepositoryImpl.KEY_MIDDAY_NOTIFICATION, true);
        verify(editorMock).apply();
        verifyNoMoreInteractions(sharedPreferencesMock, editorMock);
    }

    @Test
    public void setNotificationPreferenceToFalse_when_setMiddayNotification_with_notificationDisabled() {
        // WHEN
        preferencesRepository.setMiddayNotificationEnabled(false);

        // THEN
        verify(sharedPreferencesMock).edit();
        verify(editorMock).putBoolean(PreferencesRepositoryImpl.KEY_MIDDAY_NOTIFICATION, false);
        verify(editorMock).apply();
        verifyNoMoreInteractions(sharedPreferencesMock, editorMock);
    }
}