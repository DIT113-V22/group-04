package com.example.drawer;

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
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.SeekBar;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }

    /**
     * Test visibility of all elements on screen
     */
    @Test
    public void testVisibility() {
        onView(withId(R.id.textViewPathLength)).check(matches(isDisplayed()));
        onView(withId(R.id.clearBttn)).check(matches(isDisplayed()));
        onView(withId(R.id.downloadBttn)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadBttn)).check(matches(isDisplayed()));
        onView(withId(R.id.pixelGridA)).check(matches(isDisplayed()));
        onView(withId(R.id.runButton)).check(matches(isDisplayed()));
        onView(withId(R.id.numberViewCellLength)).check(matches(isDisplayed()));
        onView(withId(R.id.numberViewCellSize)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewSpeed)).check(matches(isDisplayed()));
        onView(withId(R.id.seekbar)).check(matches(isCompletelyDisplayed()));
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

    /**
     * Test if the buttons are clickable
     */
    @Test
    public void testButtons() {
        onView(withId(R.id.clearBttn)).check(matches(isClickable()));
        onView(withId(R.id.uploadBttn)).check(matches(isClickable()));
        onView(withId(R.id.downloadBttn)).check(matches(isClickable()));
        onView(withId(R.id.runButton)).check(matches(isClickable()));
        onView(withId(R.id.ManualScreen)).check(matches(isClickable()));
        onView(withId(R.id.ReadMeScreen)).check(matches(isClickable()));
        onView(withId(R.id.DrawScreen)).check(matches(isClickable()));
    }

    /**
     * Tests if the clear canvas button is position to the left of the download button
     * and above the canvas
     */
    @Test
    public void isClearCanvasButtonPositionedAppropriately() {
        onView(withId(R.id.clearBttn)).check(matches(isDisplayed()));
        onView(withId(R.id.clearBttn)).check(isCompletelyLeftOf(withId(R.id.downloadBttn)));
        onView(withId(R.id.clearBttn)).check(isCompletelyAbove(withId(R.id.pixelGridA)));
    }

    /**
     * Tests if the download button is position to the right of the clear canvas button
     * and to the left of the upload button
     */
    @Test
    public void isDownloadButtonBetweenClearCanvasButtonAndUploadButton() {
        onView(withId(R.id.downloadBttn)).check(matches(isDisplayed()));
        onView(withId(R.id.downloadBttn)).check(isCompletelyRightOf(withId(R.id.clearBttn)));
        onView(withId(R.id.downloadBttn)).check(isCompletelyLeftOf(withId(R.id.uploadBttn)));
    }

    /**
     * Tests if Canvas is positioned as intended in comparison to the above and below elements
     */
    @Test
    public void isCanvasPositionedAppropriately() {
        onView(withId(R.id.pixelGridA)).check(matches(isDisplayed()));

        onView(withId(R.id.pixelGridA)).check(isCompletelyBelow(withId(R.id.clearBttn)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyBelow(withId(R.id.downloadBttn)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyBelow(withId(R.id.clearBttn)));

        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.runButton)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.numberViewCellLength)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.numberViewCellSize)));

        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.ReadMeScreen)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.ManualScreen)));
        onView(withId(R.id.pixelGridA)).check(isCompletelyAbove(withId(R.id.DrawScreen)));
    }

    /**
     * Tests if the Run button is position to the appropriately in comparison to its
     * surrounding elements
     */
    @Test
    public void isRunButtonPositionedAppropriately() {
        onView(withId(R.id.runButton)).check(matches(isDisplayed()));

        onView(withId(R.id.runButton)).check(isCompletelyBelow(withId(R.id.pixelGridA)));

        onView(withId(R.id.runButton)).check(isCompletelyRightOf(withId(R.id.numberViewCellSize)));
        onView(withId(R.id.runButton)).check(isCompletelyRightOf(withId(R.id.numberViewCellLength)));

        onView(withId(R.id.runButton)).check(isCompletelyAbove(withId(R.id.ReadMeScreen)));
        onView(withId(R.id.runButton)).check(isCompletelyAbove(withId(R.id.ManualScreen)));
        onView(withId(R.id.runButton)).check(isCompletelyAbove(withId(R.id.DrawScreen)));
    }

    /**
     * Test progress of seekbar
     */
    @Test
    public void testSeekBar() {
        onView(withId(R.id.seekbar)).perform(setProgress(0));
        onView(withId(R.id.textViewSpeed)).check(matches(withText("Current speed:0")));

        onView(withId(R.id.seekbar)).perform(setProgress(50));
        onView(withId(R.id.textViewSpeed)).check(matches(withText("Current speed:50")));

        onView(withId(R.id.seekbar)).perform(setProgress(100));
        onView(withId(R.id.textViewSpeed)).check(matches(withText("Current speed:100")));
    }

    /**
     * Test editing text fields
     */
    @Test
    public void testEditTexts() {
        onView(withId(R.id.numberViewCellLength)).perform(typeText("3"), closeSoftKeyboard()).check(matches(withText("3")));
        onView(withId(R.id.numberViewCellSize)).perform(typeText("2"), closeSoftKeyboard()).check(matches(withText("2")));
    }

}
