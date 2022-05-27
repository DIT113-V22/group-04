package com.example.drawer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DrawControlInstrumentedTest {

    @Rule
    public ActivityScenarioRule<DrawControl> activityScenarioRule =
            new ActivityScenarioRule<>(DrawControl.class);

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

    }

    /**
     * Test visibility of navbar elements on screen
     */
    @Test
    public void isNavBarVisible() {
        onView(withId(R.id.ManualScreen)).check(matches(isDisplayed()));
        onView(withId(R.id.ReadMeScreen)).check(matches(isDisplayed()));
        onView(withId(R.id.DrawScreen)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether clicking the Intro Button creates an intent
     * to the Intro Activity
     */
    @Test
    public void doesIntroButtonCreateIntentToIntroScreen() {
        onView(withId(R.id.ReadMeScreen)).check(matches(withText("Intro")));
        onView(withId(R.id.ReadMeScreen)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    /**
     * Tests whether clicking the Manual Control Button creates an intent
     * to the Manual Control Activity
     */
    @Test
    public void doesManualControlButtonCreateIntentToManualControlScreen() {
        onView(withId(R.id.ManualScreen)).check(matches(withText("Manual Control")));
        onView(withId(R.id.ManualScreen)).perform(click());
        intended(hasComponent(ManualControl.class.getName()));
    }
}
