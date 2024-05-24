package com.shahab12344.loader_system;
import android.net.Uri;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.when;

public class EditProfileTest {

    private SessionManager sessionManager;

    @Before
    public void setUp() {
        sessionManager = Mockito.mock(SessionManager.class);
        when(sessionManager.getUserId()).thenReturn("12");
        when(sessionManager.getProfileImageUri()).thenReturn("images/08-05-24-1715161746-470471.jpg");
    }

    @Rule
    public ActivityScenarioRule<Booking_Activity> activityScenarioRule =
            new ActivityScenarioRule<>(Booking_Activity.class);

    @Test
    public void testeditprofile() {
        onView(withId(R.id.fnameedit)).perform(ViewActions.clearText());
        onView(withId(R.id.lnameedit)).perform(ViewActions.clearText());
        onView(withId(R.id.emailedit)).perform(ViewActions.clearText());
        onView(withId(R.id.phonedit)).perform(ViewActions.clearText());
        onView(withId(R.id.fnameedit))
                .perform(ViewActions.typeText("Shahab"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.lnameedit))
                .perform(ViewActions.typeText("Ali"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.emailedit))
                .perform(ViewActions.typeText("Shahabyousaf@gmail.com"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.phonedit))
                .perform(ViewActions.typeText("+923222222222"), ViewActions.closeSoftKeyboard());

        // Call to the method to set default profile image URI
        Uri defaultImageUri = getImageUri();

        // Set the default profile image URI in the session manager
        when(sessionManager.getProfileImageUri()).thenReturn(defaultImageUri.toString());

        onView(withId(R.id.btn_profile_edit_done)).perform(ViewActions.click());

        onView(ViewMatchers.withText("User profile updated successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Method to get URI for default profile image
    private Uri getImageUri() {
        // Load the default profile image from drawable resources
        String defaultImageUriString = "android.resource://" + ApplicationProvider.getApplicationContext().getPackageName() + "/drawable/profile_default";
        return Uri.parse(defaultImageUriString);
    }
}
