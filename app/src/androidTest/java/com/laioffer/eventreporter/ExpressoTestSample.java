package com.laioffer.eventreporter;


import android.view.View;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.util.regex.Pattern.matches;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExpressoTestSample {
    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
        MainActivity.class);

    @Before
    public void initValidString() {
        mStringToBetyped = "Expresso";
    }


    /**
     * Check if Login successfully, if yes, it should go to another EventActivity
     */
    @Test
    public void checkLoginSuccessful() {
        //Type username
        onView(withId(R.id.editTextLogin)).perform(ViewActions.clearText()).
            perform(ViewActions.typeText("alex"));

        // Type password
        onView(withId(R.id.editTextPassword)).perform(ViewActions.clearText()).
            perform(ViewActions.typeText("1234"));

        //Send intent and check intent
        onView(withId(R.id.submit)).perform(ViewActions.click());

        //Check text
        onView(withId(R.id.Events)).check(matches(withText("Events")));
    }

    @Test
    public void checkShowToast() {
        //Type username
        onView(withId(R.id.editTextLogin)).perform(ViewActions.clearText()).
            perform(ViewActions.typeText("alex"));

        // Type password
        onView(withId(R.id.editTextPassword)).perform(ViewActions.clearText()).
            perform(ViewActions.typeText("1234"));

        //Send intent and check intent
        onView(withId(R.id.submit)).perform(ViewActions.click());

        //Check text
        onView(withId(R.id.Events)).check(matches(withText("Events")));
    }

    private ViewAssertion matches(Matcher<View> events) {
        return null;
    }
}
