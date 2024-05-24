package com.shahab12344.loader_system;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import com.google.android.material.textfield.TextInputLayout;
import com.shahab12344.loader_system.Constants;
import com.shahab12344.loader_system.R;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.when;

@LargeTest
public class ComplaintFragmentTest {

    // Mocking BookingSessionManager
    private BookingSessionManager bookingSessionManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        bookingSessionManager = Mockito.mock(BookingSessionManager.class);
        when(bookingSessionManager.getKeyBookingId()).thenReturn("123");
        when(bookingSessionManager.getDriverName()).thenReturn("Shahid");
    }

    @Rule
    public ActivityScenarioRule<Booking_Activity> activityScenarioRule =
            new ActivityScenarioRule<>(Booking_Activity.class);

    @Test
    public void testComplaintRegistration() {
        // Check if complaint type spinner is displayed
        onView(withId(R.id.complaint_type)).check(matches(isDisplayed()));

        // Click on the complaint type spinner
        onView(withId(R.id.complaint_type)).perform(ViewActions.click());

        // Select a complaint type from the dropdown
        onView(ViewMatchers.withText("Wrong Route Taken"))
                .perform(ViewActions.click());

        // Enter complaint description
        onView(withId(R.id.complaintDescriptionEditText))
                .perform(ViewActions.typeText("Test complaint description"), ViewActions.closeSoftKeyboard());

        // Click on submit button
        onView(withId(R.id.btn_complaint)).perform(ViewActions.click());

        // Check if toast message is displayed
        onView(ViewMatchers.withText("Complaint registered successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyFieldValidation() {

        // Click on the submit button without entering complaint description
        onView(withId(R.id.btn_complaint)).perform(ViewActions.click());
        // Check if error message is displayed for empty description field
        onView(withId(R.id.complaint_text))
                .check(matches(new TypeSafeMatcher<View>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("with error text: \"Description cannot be empty\"");
                    }

                    @Override
                    protected boolean matchesSafely(View view) {
                        if (view instanceof TextInputLayout) {
                            TextInputLayout textInputLayout = (TextInputLayout) view;
                            CharSequence error = textInputLayout.getError();
                            return error != null && error.toString().equals("Description cannot be empty");
                        }
                        return false;
                    }
                }));
    }

    @Test
    public void testNoInternetConnection() {
        // Mocking internet connection error
        //Constants.URL_COMPLAINT = "http://invalid.url";

        // Perform complaint registration
        onView(withId(R.id.complaint_type)).perform(ViewActions.click());

        // Select a complaint type from the dropdown
        onView(ViewMatchers.withText("Wrong Route Taken"))
                .perform(ViewActions.click());

        // Enter complaint description
        onView(withId(R.id.complaintDescriptionEditText))
                .perform(ViewActions.typeText("Test complaint description"), ViewActions.closeSoftKeyboard());

        // Click on submit button
        onView(withId(R.id.btn_complaint)).perform(ViewActions.click());

        // Check if toast message for internet error is displayed
        onView(ViewMatchers.withText("Unable to connect to the server. Please check your internet connection."))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}
