package com.drawer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyRightOf;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.drawer.activities.DrawControl;
import com.drawer.activities.MainActivity;
import com.drawer.activities.ManualControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DrawControlInstrumentedTest {

    @Rule
    public ActivityScenarioRule<DrawControl> activityScenarioRule =
            new ActivityScenarioRule<>(DrawControl.class);

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
        onView(withId(R.id.textViewPathLength)).check(matches(isDisplayed()));
        onView(withId(R.id.clearButton)).check(matches(isDisplayed()));
        onView(withId(R.id.downloadButton)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadButton)).check(matches(isDisplayed()));
        onView(withId(R.id.pixelGridA)).check(matches(isDisplayed()));
        onView(withId(R.id.runButton)).check(matches(isDisplayed()));
        onView(withId(R.id.numberViewCellLength)).check(matches(isDisplayed()));
        onView(withId(R.id.numberViewSpeed)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewCurrentDistance)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewDistanceTraveled)).check(matches(isDisplayed()));
    }

    /**
     * Test visibility of navbar elements on screen.
     */
    @Test
    public void isNavBarVisible() {
        onView(withId(R.id.DrawNavbarManual)).check(matches(isDisplayed()));
        onView(withId(R.id.DrawNavbarMain)).check(matches(isDisplayed()));
        onView(withId(R.id.DrawNavbarMain)).check(matches(isDisplayed()));
    }

    /**
     * Tests whether clicking the Intro Button creates an intent to the Intro Activity.
     */
    @Test
    public void doesIntroButtonCreateIntentToIntroScreen() {
        onView(withId(R.id.DrawNavbarMain)).check(matches(withText("Intro")));
        onView(withId(R.id.DrawNavbarMain)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    /**
     * Tests whether clicking the Manual Control Button creates an intent to the Manual Control Activity.
     */
    @Test
    public void doesManualControlButtonCreateIntentToManualControlScreen() {
        onView(withId(R.id.DrawNavbarManual)).check(matches(withText("Manual Control")));
        onView(withId(R.id.DrawNavbarManual)).perform(click());
        intended(hasComponent(ManualControl.class.getName()));
    }

    /**
     * Test if the buttons are clickable.
     */
    @Test
    public void testButtons() {
        onView(withId(R.id.clearButton)).check(matches(isClickable()));
        onView(withId(R.id.uploadButton)).check(matches(isClickable()));
        onView(withId(R.id.downloadButton)).check(matches(isClickable()));
        onView(withId(R.id.runButton)).check(matches(isClickable()));
        onView(withId(R.id.DrawNavbarManual)).check(matches(isClickable()));
        onView(withId(R.id.DrawNavbarMain)).check(matches(isClickable()));
        onView(withId(R.id.DrawNavbarDraw)).check(matches(isClickable()));
    }

    /**
     * Tests if the clear canvas button is position to the left of the download button.
     * And above the canvas.
     */
    @Test
    public void isClearCanvasButtonPositionedAppropriately() {
        onView(withId(R.id.clearButton)).check(matches(isDisplayed()));
        onView(withId(R.id.clearButton)).check(isCompletelyLeftOf(withId(R.id.uploadButton)));
        onView(withId(R.id.clearButton)).check(isCompletelyAbove(withId(R.id.pixelGridA)));
    }

    /**
     * Tests if the download button is position to the right of the clear canvas button.
     * And to the left of the upload button.
     */
    @Test
    public void isDownloadButtonBetweenClearCanvasButtonAndUploadButton() {
        onView(withId(R.id.downloadButton)).check(matches(isDisplayed()));
        onView(withId(R.id.downloadButton)).check(isCompletelyRightOf(withId(R.id.clearButton)));
        onView(withId(R.id.downloadButton)).check(isCompletelyRightOf(withId(R.id.uploadButton)));
    }

    /**
     * Tests if Canvas is positioned as intended in comparison to the above and below elements.
     */
    @Test
    public void isCanvasPositionedAppropriately() {
        onView(withId(R.id.pixelGridA)).check(matches(isDisplayed()));

        onView(withId(R.id.pixelGridA)).check(isCompletelyBelow(withId(R.id.clearButton)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyBelow(withId(R.id.downloadButton)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyBelow(withId(R.id.uploadButton)));

        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.runButton)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.numberViewCellLength)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.numberViewSpeed)));

        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.DrawNavbarMain)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.DrawNavbarDraw)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.DrawNavbarManual)));
    }

    /**
     * Tests if the Run button is position to the appropriately in comparison to its surrounding elements.
     */
    @Test
    public void isRunButtonPositionedAppropriately() {
        onView(withId(R.id.runButton)).check(matches(isDisplayed()));

        onView(withId(R.id.runButton)).check(isCompletelyBelow(withId(R.id.pixelGridA)));

        onView(withId(R.id.runButton)).check(isCompletelyRightOf(withId(R.id.numberViewSpeed)));
        onView(withId(R.id.runButton)).check(isCompletelyRightOf(withId(R.id.numberViewCellLength)));

        onView(withId(R.id.runButton)).check(isCompletelyAbove(withId(R.id.DrawNavbarMain)));
        onView(withId(R.id.runButton)).check(isCompletelyAbove(withId(R.id.DrawNavbarManual)));
        onView(withId(R.id.runButton)).check(isCompletelyAbove(withId(R.id.DrawNavbarDraw)));
    }

    /**
     * Test editing text fields.
     */
    @Test
    public void testEditTexts() {
        onView(withId(R.id.numberViewCellLength))
                .perform(typeText("3"),
                closeSoftKeyboard()).check(matches(withText("3")));
        onView(withId(R.id.numberViewSpeed))
                .perform(typeText("2"),
                closeSoftKeyboard()).check(matches(withText("2")));
    }

}
