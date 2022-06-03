package com.drawer;

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
public class MainActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

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
     * Test visibility of all elements on screen.
     */
    @Test
    public void testVisibility() {
        onView(withId(R.id.connectButton)).check(matches(isDisplayed()));
        onView(withId(R.id.subscribeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.publishButton)).check(matches(isDisplayed()));
        onView(withId(R.id.disconnectButton)).check(matches(isDisplayed()));
    }

    /**
     * Test visibility of navbar elements on screen.
     */
    @Test
    public void isNavBarVisible() {
        onView(withId(R.id.MainNavbarManual)).check(matches(isDisplayed()));
        onView(withId(R.id.MainNavbarMain)).check(matches(isDisplayed()));
        onView(withId(R.id.MainNavbarDraw)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether clicking the Draw Control Button creates an intent to the Draw Control Activity.
     */
    @Test
    public void doesDrawControlButtonCreateIntentToDrawControlScreen() {
        onView(withId(R.id.MainNavbarDraw)).check(matches(withText("Draw Control")));
        onView(withId(R.id.MainNavbarDraw)).perform(click());
        intended(hasComponent(DrawControl.class.getName()));
    }

    /**
     * Tests whether clicking the Manual Control Button creates an intent to the Manual Control Activity.
     */
    @Test
    public void doesManualControlButtonCreateIntentToManualControlScreen() {
        onView(withId(R.id.MainNavbarManual)).check(matches(withText("Manual Control")));
        onView(withId(R.id.MainNavbarManual)).perform(click());
        intended(hasComponent(ManualControl.class.getName()));
    }

    /**
     * Tests basic clicking functionality of mqtt buttons.
     */
    @Test
    public void testMqttButtonClicks() {
        onView(withId(R.id.subscribeButton)).perform(click());
        onView(withId(R.id.publishButton)).perform(click());
        onView(withId(R.id.disconnectButton)).perform(click());
    }
}
