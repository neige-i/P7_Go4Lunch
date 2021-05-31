package com.neige_i.go4lunch.view.home;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.neige_i.go4lunch.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> activityScenarioRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void displayCorrectTitleAndFragment() {
        // GIVEN
        // WHEN

        // THEN: map is the default displayed fragment
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.title_restaurant))));
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // WHEN
        onView(withId(R.id.action_restaurant)).perform(click());

        // THEN
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.title_restaurant))));
        // R.id.distance_lbl is the ID of a view only available in the "restaurant list" fragment
        onView(allOf(withId(R.id.recyclerview), isDisplayed())).check(matches(hasDescendant(withId(R.id.distance_lbl))));

        // WHEN
        onView(withId(R.id.action_workmates)).perform(click());

        // THEN
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.title_workmates))));
        // R.id.workmate_restaurant_lbl is the ID of a view only available in the "workmate list" fragment
        onView(allOf(withId(R.id.recyclerview), isDisplayed())).check(matches(hasDescendant(withId(R.id.workmate_restaurant_lbl))));

        // WHEN
        onView(withId(R.id.action_map)).perform(click());

        // THEN
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.title_restaurant))));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}