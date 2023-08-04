package com.example.pantryinventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityScenarioRule<Login> activityRule = new ActivityScenarioRule<>(Login.class);

//    @Test
//    public void successfulLoginStartsMainActivity() {
//        // Initialize Espresso-Intents before your test.
//        Intents.init();
//
//        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("12345678@gmail.com"));
//        onView(withId(R.id.editTextTextPassword)).perform(typeText("12345678"));
//        onView(withId(R.id.login_button)).perform(click());
//
//        // Verify that an intent to start MainActivity was sent.
//        intended(hasComponent(MainActivity.class.getName()));
//
//        // Release Espresso-Intents after test completes.
//        Intents.release();
//    }

    @Test
    public void createAccountButtonStartsRegistration() {
        // Initialize Espresso-Intents before the test.
        Intents.init();

        onView(withId(R.id.create_account_button)).perform(click());

        // Verify that an intent to start Registration activity was sent.
        intended(hasComponent(Registration.class.getName()));

        // Release Espresso-Intents after test completes.
        Intents.release();
    }
}
