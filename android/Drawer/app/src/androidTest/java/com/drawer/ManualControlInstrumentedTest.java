package com.drawer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.drawer.activities.DrawControl;
import com.drawer.activities.MainActivity;
import com.drawer.activities.ManualControl;
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
     * Initialise Espresso Intents capturing before each test.
     */
    @Before
    public void intentsInit() {
        Intents.init();
    }

    /**
     * Release Espresso Intents capturing after each test.
     */
    @After
    public void intentsTeardown() {
        Intents.release();
    }

    /**
     * Tests if the text that displays the speed is positioned above the text that displays the angle.
     */
    @Test
    public void isSpeedTextDisplayedAboveAngleText() {
        onView(withId(R.id.speedStat)).check(matches(isDisplayed()));
        onView(withId(R.id.speedStat)).check(isCompletelyAbove(withId(R.id.angleStat)));
    }

    /**
     * Tests if the Joystick is placed below both text boxes.
     */
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
     * Test visibility of navbar elements on screen.
     */
    @Test
    public void isNavBarVisible() {
        onView(withId(R.id.ManualNavbarManual)).check(matches(isDisplayed()));
        onView(withId(R.id.ManualNavbarMain)).check(matches(isDisplayed()));
        onView(withId(R.id.ManualNavbarDraw)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether clicking the Draw Control Button creates an intent to the Draw Control Activity.
     */
    @Test
    public void doesDrawControlButtonCreateIntentToDrawControlScreen() {
        onView(withId(R.id.ManualNavbarDraw)).check(matches(withText("Draw Control")));
        onView(withId(R.id.ManualNavbarDraw)).perform(click());
        intended(hasComponent(DrawControl.class.getName()));
    }

    /**
     * Tests whether clicking the Intro Button creates an intent to the Intro Activity.
     */
    @Test
    public void doesIntroButtonCreateIntentToIntroScreen() {
        onView(withId(R.id.ManualNavbarMain)).check(matches(withText("Intro")));
        onView(withId(R.id.ManualNavbarMain)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }
}
