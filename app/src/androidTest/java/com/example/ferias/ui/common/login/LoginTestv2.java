package com.example.ferias.ui.common.login;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.ferias.MainActivity;
import com.example.ferias.R;
import com.example.ferias.data.common.User;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoginTestv2 {
    Login login = new Login();

    @Test
    public void OnClickTests() {
        ActivityScenario activityScenario= ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.etEmail_Login))
                .perform(typeText("mitch6@sciencejrq.com"), closeSoftKeyboard());

        onView(withId(R.id.etPassword_Login))
                .perform(typeText("mitch6@sciencejrq.com"), closeSoftKeyboard());
    }
    @Test
    public void testNavigationFromLogInTORegister() {

        // Create a TestNavHostController
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        // Create a graphical FragmentScenario for the TitleScreen
        FragmentScenario<Login> titleScenario = FragmentScenario.launchInContainer(Login.class, null, R.style.Theme_MaterialComponents, null);

        titleScenario.onFragment(fragment ->
                {    // Set the graph on the TestNavHostController
                    navController.setGraph(R.navigation.mobile_navigation);

                    // Make the NavController available via the findNavController() APIs
                    Navigation.setViewNavController(fragment.requireView(), navController);
                }
        );

        onView(ViewMatchers.withId(R.id.bt_Register)).perform(click());
        assertEquals(navController.getCurrentDestination().getLabel(), "fragment_registration");
    }

    @Test
    public void testNavigationToInLogIn() {
        // Create a TestNavHostController
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        // Create a graphical FragmentScenario for the TitleScreen
        FragmentScenario<Login> titleScenario = FragmentScenario.launchInContainer(Login.class, null, R.style.Theme_MaterialComponents, null);

        titleScenario.onFragment(fragment ->
                {    // Set the graph on the TestNavHostController
                    navController.setGraph(R.navigation.mobile_navigation);

                    // Make the NavController available via the findNavController() APIs
                    Navigation.setViewNavController(fragment.requireView(), navController);
                }
        );

        onView(withId(R.id.etEmail_Login))
                .perform(clearText());

        onView(withId(R.id.etPassword_Login))
                .perform(clearText());

        onView(withId(R.id.etEmail_Login))
                .perform(typeText("mitch6@sciencejrq.com"), closeSoftKeyboard());

        onView(withId(R.id.etPassword_Login))
                .perform(typeText("mitch6@sciencejrq.com"), closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.bt_Login)).perform(click());
        Log.e(" ", navController.getCurrentDestination().getLabel().toString());

        //if runned with debugger the passes because it waits for the log in to be completed otherwise it fails not having time to change fragment
        assertEquals(navController.getCurrentDestination().getLabel(), "traveler_fragment_home");
    }
}