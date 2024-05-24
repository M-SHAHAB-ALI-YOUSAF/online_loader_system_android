package com.shahab12344.loader_system;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class completebookingtest {
    private BookingSessionManager bookingSessionManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        bookingSessionManager = Mockito.mock(BookingSessionManager.class);
        when(bookingSessionManager.getKeyBookingId()).thenReturn("123");
    }

    @Rule
    public ActivityScenarioRule<Booking_Activity> activityRule =
            new ActivityScenarioRule<>(Booking_Activity.class);


    @Test
    public void testcompletebooking() {


        onView(withId(R.id.buttonridefinish)).perform(ViewActions.click());

        onView(withText("Booking Complete successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }


    @Test
    public void nointernet(){
        Constants.URL_UPDATE_BOOKING_STATUS = "http://invalid.url";

        // Click on submit button
        onView(withId(R.id.buttonridefinish)).perform(ViewActions.click());

        // Check if toast message for internet error is displayed
        onView(ViewMatchers.withText("No internet connection"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

}