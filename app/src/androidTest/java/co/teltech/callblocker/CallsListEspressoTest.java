package co.teltech.callblocker;

import android.os.Build;

import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.teltech.callblocker.activities.CallsListActivity;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class CallsListEspressoTest {

    @Rule
    public ActivityTestRule<CallsListActivity> activityTestRule =
            new ActivityTestRule<>(CallsListActivity.class);

    @Before
    public void grantPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.CALL_PHONE");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.READ_CALL_LOG");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.READ_CONTACTS");
        }
    }

    @Test
    public void checkUIComponents() {
        onView(withText(R.string.label_filter_calls)).check(matches(isDisplayed()));
        onView(withId(R.id.switchBlockCalls)).check(matches(isDisplayed()));
    }

    @Test
    public void turnOnCallBlocking() {
//        onView(withId(R.id.switchBlockCalls)).check(matches(isDisplayed()));
        //todo perform check if block call is turned off
        onView(withId(R.id.switchBlockCalls)).perform(click());
        onView(withId(R.id.filterOptionsContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.switchBlockCalls)).perform(click());
    }

    @Test
    public void switchBlockCallsDefaultRadioButtonSpamOnlyCheck() {
        //todo perform check if block call is turned off
        onView(withId(R.id.switchBlockCalls)).perform(click());

        onView(withId(R.id.radioOptionSpamOnly)).perform(click());
        onView(withId(R.id.radioOptionSpamOnly)).check(matches(isChecked()));
        onView(withId(R.id.radioOptionNotContacts)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionBlacklist)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionWhitelist)).check(matches(not(isChecked())));

        onView(withId(R.id.switchBlockCalls)).perform(click());
    }

    @Test
    public void switchBlockCallsDefaultRadioButtonNotContactsCheck() {
        //todo perform check if block call is turned off
        onView(withId(R.id.switchBlockCalls)).perform(click());

        onView(withId(R.id.radioOptionNotContacts)).perform(click());
        onView(withId(R.id.radioOptionSpamOnly)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionNotContacts)).check(matches((isChecked())));
        onView(withId(R.id.radioOptionBlacklist)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionWhitelist)).check(matches(not(isChecked())));

        onView(withId(R.id.switchBlockCalls)).perform(click());
    }

    @Test
    public void switchBlockCallsDefaultRadioButtonBlacklistCheck() {
        //todo perform check if block call is turned off
        onView(withId(R.id.switchBlockCalls)).perform(click());

        onView(withId(R.id.radioOptionBlacklist)).perform(click());
        onView(withId(R.id.radioOptionSpamOnly)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionNotContacts)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionBlacklist)).check(matches(isChecked()));
        onView(withId(R.id.radioOptionWhitelist)).check(matches(not(isChecked())));

        onView(withId(R.id.buttonEditBlacklist)).check(matches(isDisplayed()));

        onView(withId(R.id.switchBlockCalls)).perform(click());
    }

    @Test
    public void switchBlockCallsDefaultRadioButtonWhitelistCheck() {
        //todo perform check if block call is turned off
        onView(withId(R.id.switchBlockCalls)).perform(click());

        onView(withId(R.id.radioOptionWhitelist)). perform(click());
        onView(withId(R.id.radioOptionSpamOnly)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionNotContacts)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionBlacklist)).check(matches(not(isChecked())));
        onView(withId(R.id.radioOptionWhitelist)).check(matches(isChecked()));

        onView(withId(R.id.buttonEditWhitelist)).check(matches(isDisplayed()));

        onView(withId(R.id.switchBlockCalls)).perform(click());
    }


}
