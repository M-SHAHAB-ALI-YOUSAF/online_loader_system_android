//package com.shahab12344.loader_system;
//
//import androidx.fragment.app.testing.FragmentScenario;
//import androidx.test.espresso.Espresso;
//import androidx.test.espresso.IdlingRegistry;
//import androidx.test.espresso.IdlingResource;
//import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.assertion.ViewAssertions;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.filters.LargeTest;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class DriverRequestFragmentTest {
//
//    private IdlingResource idlingResource;
//
//    @Before
//    public void registerIdlingResource() {
//        // Register an idling resource to wait for data fetching to complete
//        idlingResource = new DataFetchingIdlingResource();
//        IdlingRegistry.getInstance().register(idlingResource);
//    }
//
//    @After
//    public void unregisterIdlingResource() {
//        // Unregister the idling resource after the test
//        if (idlingResource != null) {
//            IdlingRegistry.getInstance().unregister(idlingResource);
//        }
//    }
//
//    @Test
//    public void testDriversFetchedAndRecyclerViewDisplayed() {
//        // Start the fragment scenario
//        FragmentScenario<fragment_driver_request> scenario = FragmentScenario.launchInContainer(fragment_driver_request.class);
//
//        // Perform actions to wait for data fetching to complete and verify RecyclerView is displayed
//        Espresso.onView(ViewMatchers.withId(R.id.driverRecyclerView))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
//    }
//
//    @Test
//    public void testDataFetchingErrorToastDisplayed() {
//        // Mock an error response from data fetching
//        Constants.URL_available_Drivers = "http://invalid.url";
//
//        // Start the fragment scenario
//        FragmentScenario<fragment_driver_request> scenario = FragmentScenario.launchInContainer(fragment_driver_request.class);
//
//        // Perform actions to wait for data fetching error toast to be displayed
//        Espresso.onView(ViewMatchers.withText("Error: Unable to fetch drivers"))
//                .inRoot(new ToastMatcher())
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
//    }
//}
