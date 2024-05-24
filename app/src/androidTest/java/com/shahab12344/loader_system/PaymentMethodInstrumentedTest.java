package com.shahab12344.loader_system;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class PaymentMethodInstrumentedTest {

    @Rule
    public ActivityScenarioRule<PaymentMethod> activityScenarioRule = new ActivityScenarioRule<>(PaymentMethod.class);

    @Test
    public void testPaymentSheetInitialization() {
        // Check that the progress dialog is displayed initially
        Espresso.onView(withText("Please wait.....")).check(matches(isDisplayed()));

        // Wait for 10 seconds to allow PaymentSheet initialization
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check that the progress dialog is dismissed
        Espresso.onView(withText("Please wait.....")).check(matches(not(isDisplayed())));
    }

    @Test
    public void testSendDataToDatabase() {
        // Wait for 10 seconds to allow PaymentSheet initialization
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Simulate successful completion of PaymentSheet
        // Here, you might need to perform actions to complete the payment,
        // such as clicking on a button or interacting with UI elements.
        // Depending on your app's logic, you can simulate these actions using Espresso.

        // Verify that data is sent to the database
        // For example, you can check if a success message is displayed after data is sent,
        // or you can check if the database is updated with the expected values.
        // This depends on how your app handles sending data to the database after PaymentSheet completion.
    }
}
