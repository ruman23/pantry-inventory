package com.example.pantryinventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;
import androidx.test.core.app.ActivityScenario;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@RunWith(AndroidJUnit4.class)
public class LoginTest {
    @Mock
    FirebaseAuth mockAuth;

    @Mock
    LoginManager.LoginCallback callback;

    @Rule
    public ActivityScenarioRule<Login> activityScenarioRule =
            new ActivityScenarioRule<>(Login.class);
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void signIn_withValidCredentials_callsOnSuccess() {
        // Given
        LoginManager manager = new LoginManager(mockAuth);
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(Tasks.forResult(mock(AuthResult.class)));

        // When
        manager.signIn("test@test.com", "password123", callback);

        // Then
        verify(callback).onSuccess();
    }

    @Test
    public void signIn_withInvalidCredentials_callsOnFailure() {
        // Given
        LoginManager manager = new LoginManager(mockAuth);
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(Tasks.forException(new FirebaseAuthException("ERROR", "Mock error")));

        // When
        manager.signIn("invalid@invalid.com", "invalid", callback);


        // Then
        verify(callback).onFailure();
    }

    @Test
    public void createAccountButtonStartsRegistration() {
        // Initialize Espresso-Intents before your test.
        Intents.init();

        onView(withId(R.id.create_account_button)).perform(click());

        // Verify that an intent to start Registration activity was sent.
        intended(hasComponent(Registration.class.getName()));

        // Release Espresso-Intents after test completes.
        Intents.release();
    }

}