package com.neige_i.go4lunch;

import androidx.lifecycle.LiveData;

public class LiveDataTestUtils {

    public static <T> T getValueForTesting(final LiveData<T> liveData) {
        liveData.observeForever(t -> {
        });
        return liveData.getValue();
    }

    public static <T> int getLiveDataTriggerCount(final LiveData<T> liveData) {
        final int[] called = new int[]{0};
        liveData.observeForever(t -> {
            called[0]++;
        });
        return called[0];
    }
}
