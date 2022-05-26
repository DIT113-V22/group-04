package com.example.drawer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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

    }
}
