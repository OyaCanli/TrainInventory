package com.canli.oya.traininventoryroom.endtoendtests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.DataBindingIdlingResource
import com.canli.oya.traininventoryroom.utils.hasSelectedItem
import com.canli.oya.traininventoryroom.utils.monitorActivity
import com.canli.oya.traininventoryroom.utils.withIconResource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationTests {

    // An Idling Resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun bottomNavigation_correctItemsAreSetSelected(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Verify category item is selected by default and category list fragment is on screen
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.categories)))
        onView(withText(R.string.all_categories)).check(matches(withParent(withId(R.id.toolbar))))

        //Click on brands menu item and verify that brands frag is shown and brand menu item seems selected
        onView(withId(R.id.brands)).perform(click())
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.brands)))
        onView(withText(R.string.all_brands)).check(matches(withParent(withId(R.id.toolbar))))

        //Click on trains menu item and verify that trains frag is shown and train menu item seems selected
        onView(withId(R.id.trains)).perform(click())
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.trains)))
        onView(withText(R.string.all_trains)).check(matches(withParent(withId(R.id.toolbar))))

        //Press back and verify that we are back at brands screen and brand menu item gets selected
        Espresso.pressBack()
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.brands)))
        onView(withText(R.string.all_brands)).check(matches(withParent(withId(R.id.toolbar))))

        /*Press back again  and verify that we are back at categories screen
        and category menu item gets selected*/
        Espresso.pressBack()
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.categories)))
        onView(withText(R.string.all_categories)).check(matches(withParent(withId(R.id.toolbar))))

        activityScenario.close()
    }
    
    @Test
    fun clickedPlusIcon_BecomesCancelIcon_andStaysThatWay() = runBlocking {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        /*Verify that + icon is shown on the action menu at launch. Then click on plus button.
        Verify that + icon is replaced with x icon is shown*/
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_plus_to_cross)))
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))

        //Then switch to brands frag and verify that + icon is shown on the action menu
        onView(withId(R.id.brands)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_plus_to_cross)))

        //Click on plus item and verify it becomes x. Then click back and verify that it return back to plus
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_plus_to_cross)))

        //Then press back and verify that category frag still shows x icon as it was left
        Espresso.pressBack()
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))

        activityScenario.close()
    }
}