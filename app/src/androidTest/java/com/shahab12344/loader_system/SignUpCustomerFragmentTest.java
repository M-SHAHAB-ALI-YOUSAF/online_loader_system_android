package com.shahab12344.loader_system;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class SignUpCustomerFragmentTest {

    @Rule
    public ActivityScenarioRule<Login_Registration> activityScenarioRule = new ActivityScenarioRule<>(Login_Registration.class);

    @Test
    public void testEmptyFieldsValidation() {
        // Start the fragment
        ActivityScenario<Login_Registration> scenario = activityScenarioRule.getScenario();
        Espresso.onView(ViewMatchers.withId(R.id.customerCardView)).perform(ViewActions.click());
        Espresso.closeSoftKeyboard();
        // Perform click on register button without entering any data
        Espresso.onView(ViewMatchers.withId(R.id.reg_btn)).perform(ViewActions.click());

        onView(withId(R.id.reg_name))
                .check(matches(new TypeSafeMatcher<View>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("with error text: \"First name is required\"");
                    }

                    @Override
                    protected boolean matchesSafely(View view) {
                        if (view instanceof TextInputLayout) {
                            TextInputLayout textInputLayout = (TextInputLayout) view;
                            CharSequence error = textInputLayout.getError();
                            return error != null && error.toString().equals("First name is required");
                        }
                        return false;
                    }
                }));

        onView(withId(R.id.lastname))
                .check(matches(new TypeSafeMatcher<View>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("with error text: \"Last name is required\"");
                    }

                    @Override
                    protected boolean matchesSafely(View view) {
                        if (view instanceof TextInputLayout) {
                            TextInputLayout textInputLayout = (TextInputLayout) view;
                            CharSequence error = textInputLayout.getError();
                            return error != null && error.toString().equals("Last name is required");
                        }
                        return false;
                    }
                }));

        onView(withId(R.id.reg_email))
                .check(matches(new TypeSafeMatcher<View>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("with error text: \"Email is required\"");
                    }

                    @Override
                    protected boolean matchesSafely(View view) {
                        if (view instanceof TextInputLayout) {
                            TextInputLayout textInputLayout = (TextInputLayout) view;
                            CharSequence error = textInputLayout.getError();
                            return error != null && error.toString().equals("Email is required");
                        }
                        return false;
                    }
                }));

        onView(withId(R.id.reg_phoneNo))
                .check(matches(new TypeSafeMatcher<View>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("with error text: \"Invalid Phone No pattern\"");
                    }

                    @Override
                    protected boolean matchesSafely(View view) {
                        if (view instanceof TextInputLayout) {
                            TextInputLayout textInputLayout = (TextInputLayout) view;
                            CharSequence error = textInputLayout.getError();
                            return error != null && error.toString().equals("Invalid Phone No pattern");
                        }
                        return false;
                    }
                }));

    }

    @Test
    public void testAlreadyRegistered() {

        Espresso.onView(ViewMatchers.withId(R.id.customerCardView)).perform(ViewActions.click());
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.fname))
                .perform(ViewActions.typeText("shahab"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.lname))
                .perform(ViewActions.typeText("ali"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.email))
                .perform(ViewActions.typeText("kk@gmai.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.phonereg))
                .perform(ViewActions.typeText("3222222222"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.reg_btn)).perform(ViewActions.click());

        onView(withId(R.id.reg_phoneNo))
                .check(matches(new TypeSafeMatcher<View>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("with error text: \"Phone number is already registered\"");
                    }

                    @Override
                    protected boolean matchesSafely(View view) {
                        if (view instanceof TextInputLayout) {
                            TextInputLayout textInputLayout = (TextInputLayout) view;
                            CharSequence error = textInputLayout.getError();
                            return error != null && error.toString().equals("Phone number is already registered");
                        }
                        return false;
                    }
                }));

    }
}

