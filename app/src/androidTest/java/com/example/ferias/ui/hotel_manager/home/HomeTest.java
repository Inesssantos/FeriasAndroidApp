package com.example.ferias.ui.hotel_manager.home;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.ferias.R;
import com.example.ferias.ui.common.login.Login;
import com.example.ferias.ui.traveler.home.Home;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

//run this on debug
public class HomeTest {
    TestNavHostController navController;
    @Test
    public void testInputLocation()
    {
        NavigateToHomeFragment();

        String location = "Lisboa";
        onView(withId(R.id.textinput_location)).check(matches(isDisplayed()));
        onView(withId(R.id.textinput_location))
                .perform(typeText("Coimbra"), closeSoftKeyboard());

        onView(withId(R.id.textinput_location)).check(matches(withText("Coimbra")));
    }

    @Test
    public void testNavigationToFavorites()
    {
        NavigateToHomeFragment();

        onView(withId(R.id.my_favs_btn2)).perform(click());
        Log.e("",navController.getCurrentDestination().getLabel().toString());
        assertEquals(navController.getCurrentDestination().getLabel(), "fragment_favorites");

    }

    @Test
    public void testNavigationToBookings()
    {
        NavigateToHomeFragment();

        onView(withId(R.id.my_bookings_btn)).perform(click());
        Log.e("",navController.getCurrentDestination().getLabel().toString());
        assertEquals(navController.getCurrentDestination().getLabel(), "fragment_bookings");

    }

    void NavigateToHomeFragment()
    {
        // Create a TestNavHostController
        navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        // Create a graphical FragmentScenario for the TitleScreen
        FragmentScenario<Home> titleScenario = FragmentScenario.launchInContainer(Home.class, null, R.style.Theme_MaterialComponents, null);

        titleScenario.onFragment(fragment ->
                {    // Set the graph on the TestNavHostController
                    navController.setGraph(R.navigation.mobile_navigation);

                    // Make the NavController available via the findNavController() APIs
                    Navigation.setViewNavController(fragment.requireView(), navController);
                    navController.navigate(R.id.traveler_home);
                }
        );

        Log.e("",navController.getCurrentDestination().getLabel().toString());
        assertEquals(navController.getCurrentDestination().getLabel(), "traveler_fragment_home");

    }

}