package com.example.drawer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
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
public class ManualControlInstrumentedTest {

    @Rule
    public ActivityScenarioRule<ManualControl> activityScenarioRule =
            new ActivityScenarioRule<>(ManualControl.class);

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

    @Test
    public void isSpeedTextDisplayedAboveAngleText() {
        onView(withId(R.id.speedStat)).check(matches(isDisplayed()));
        onView(withId(R.id.speedStat)).check(isCompletelyAbove(withId(R.id.angleStat)));
    }

    @Test
    public void isJoystickDisplayedBelowSpeedAndAngle() {
        onView(withId(R.id.outerCircle)).check(matches(isDisplayed()));
        onView(withId(R.id.innerCircle)).check(matches(isDisplayed()));

        onView(withId(R.id.outerCircle)).check(isCompletelyBelow(withId(R.id.speedStat)));
        onView(withId(R.id.outerCircle)).check(isCompletelyBelow(withId(R.id.speedStat)));
        onView(withId(R.id.innerCircle)).check(isCompletelyBelow(withId(R.id.angleStat)));
        onView(withId(R.id.innerCircle)).check(isCompletelyBelow(withId(R.id.angleStat)));
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
}
