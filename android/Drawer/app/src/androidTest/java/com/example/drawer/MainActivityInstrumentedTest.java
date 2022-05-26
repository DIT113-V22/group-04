package com.example.drawer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Rule public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Initialise Espresso Intents capturing before each test
     */
    @Before
    public void intentsInit() {
        Intents.init();
    }

    /**
     * Release Espresso Intents capturing after each test
     */
    @After
    public void intentsTeardown() {
        Intents.release();
    }

    /**
     * Test visibility of all elements on screen
     */
    @Test
    public void testVisibility() {
        onView(withId(R.id.conBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.subBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.pubBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.disBtn)).check(matches(isDisplayed()));
    }

    /**
     * Test visibility of navbar elements on screen
     */
    @Test
    public void isNavBarVisible() {

    }

    @Test
    public void testMqttButtonClicks() {
        onView(withId(R.id.conBtn)).perform(click());
        onView(withId(R.id.subBtn)).perform(click());
        onView(withId(R.id.pubBtn)).perform(click());
        onView(withId(R.id.disBtn)).perform(click());
    }
}
