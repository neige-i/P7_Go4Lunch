package com.neige_i.go4lunch.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.primitives.Ints;

import org.junit.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class WorkmatesDelegateTest {

    // --------------------------------------- DEPENDENCIES ----------------------------------------

    private final Clock clock_02_11_2021 = Clock.fixed(
        LocalDate.of(2021, 11, 2).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private final WorkmatesDelegate workmatesDelegate = new WorkmatesDelegate(clock_02_11_2021);

    // -------------------------------- MOVE TO FIRST POSITION TEST --------------------------------

    @Test
    public void changeListOrder_when_moveToFirstPosition_with_matchedPredicate() {
        // GIVEN
        final List<Integer> integerList = new ArrayList<>(Ints.asList(1, 2, 3, 4, 5));

        // WHEN
        workmatesDelegate.moveToFirstPosition(integerList, integer -> integer == 4);

        // THEN
        assertEquals(
            Ints.asList(4, 1, 2, 3, 5), // 4 is in first position because it matches the predicate
            integerList
        );
    }

    @Test
    public void keepListUnchanged_when_moveToFirstPosition_with_unmatchedPredicate() {
        // GIVEN
        final List<Integer> integerList = new ArrayList<>(Ints.asList(1, 2, 3, 4, 5));

        // WHEN
        workmatesDelegate.moveToFirstPosition(integerList, integer -> integer == 6);

        // THEN
        assertEquals(
            Ints.asList(1, 2, 3, 4, 5), // Unchanged, no integer equals 6
            integerList
        );
    }

    // --------------------------------------- IS TODAY TEST ---------------------------------------

    @Test
    public void returnFalse_when_getTodayDate_with_nullValue() {
        // WHEN
        final boolean isSelected = workmatesDelegate.isToday(null);

        // THEN
        assertFalse(isSelected);
    }

    @Test
    public void returnTrue_when_getTodayDate_with_todayValue() {
        // WHEN
        final boolean isSelected = workmatesDelegate.isToday("02/11/2021");

        // THEN
        assertTrue(isSelected);
    }

    @Test
    public void returnFalse_when_getTodayDate_with_otherValue() {
        // WHEN
        final boolean isSelected = workmatesDelegate.isToday("03/11/2021");

        // THEN
        assertFalse(isSelected);
    }
}