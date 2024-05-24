package com.shahab12344.loader_system;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class OTPIsSentTest {

    @Rule
    public ActivityScenarioRule<Login_Registration> activityRule =
            new ActivityScenarioRule<>(Login_Registration.class);

    @Test
    public void testOTPIsSent() {
        // Enter the phone number
        onView(withId(R.id.enterphone))
                .perform(ViewActions.typeText("3222222222"));

        // Close the soft keyboard
        Espresso.closeSoftKeyboard();

        // Click on the button to select the role as "Customer"
        onView(withId(R.id.customerCardView)).perform(ViewActions.click());

        // Click on the button to send OTP (assuming the button has ID R.id.get_otp)
        onView(withId(R.id.get_otp)).perform(ViewActions.click());

        onView(withText("OTP IS SEND"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}
