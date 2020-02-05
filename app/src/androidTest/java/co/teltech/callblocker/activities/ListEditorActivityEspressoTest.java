package co.teltech.callblocker.activities;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.teltech.callblocker.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class ListEditorActivityEspressoTest {

    @Rule
    public ActivityTestRule<ListEditorActivity> activityTestRule =
            new ActivityTestRule<>(ListEditorActivity.class, true, true);

//    ContactHelper contactHelper = new ContactHelper(activityTestRule);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_CONTACTS",
                    "android.permission.CALL_PHONE",
                    "android.permission.READ_CALL_LOG",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.ANSWER_PHONE_CALLS");

    @Test
    public void checkUIComponents() {
        Intent i = new Intent();
        i.putExtra(ListEditorActivity.EXTRA_LIST_TYPE, ListEditorActivity.EXTRA_LIST_TYPE_BLACKLIST);
        activityTestRule.launchActivity(i);
        onView(withId(R.id.emptyListLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testContactPickerResult() {
        Intent i = new Intent();
        i.putExtra(ListEditorActivity.EXTRA_LIST_TYPE, ListEditorActivity.EXTRA_LIST_TYPE_BLACKLIST);
        activityTestRule.launchActivity(i);
        Intents.init();

        Intent resultData = new Intent();
        resultData.putExtra("phone", "12345678");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage("com.android.contacts")).respondWith(result);
        Intents.release();

    }

    @Test
    public void checkIfContactExistsInList() {
        Intent i = new Intent();
        i.putExtra(ListEditorActivity.EXTRA_LIST_TYPE, ListEditorActivity.EXTRA_LIST_TYPE_BLACKLIST);
        activityTestRule.launchActivity(i);

        onView(allOf(withId(R.id.buttonRemoveContact),
                childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 0),
                isDisplayed())).perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

}
