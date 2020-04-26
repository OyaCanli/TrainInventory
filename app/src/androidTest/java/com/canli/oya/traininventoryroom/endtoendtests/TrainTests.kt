package com.canli.oya.traininventoryroom.endtoendtests

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.datasource.sampleBrand1
import com.canli.oya.traininventoryroom.datasource.sampleCategory1
import com.canli.oya.traininventoryroom.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.datasource.sampleTrain3
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.inmemory.DaggerInMemoryTestComponent
import com.canli.oya.traininventoryroom.di.inmemory.InMemoryTestComponent
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.DataBindingIdlingResource
import com.canli.oya.traininventoryroom.utils.isGone
import com.canli.oya.traininventoryroom.utils.isVisible
import com.canli.oya.traininventoryroom.utils.monitorActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
class TrainTests {

    // An Idling Resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Inject
    lateinit var database: TrainDatabase

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<TrainApplication>()
        ComponentProvider.getInstance(app).daggerComponent = DaggerInMemoryTestComponent.builder()
                .testAppModule(TestAppModule(app))
                .build()
        (ComponentProvider.getInstance(app).daggerComponent as InMemoryTestComponent).inject(this)
    }

    @After
    fun closeDb() = database.close()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun clickAddTrain_isLaunchedWithEmptyAndDefaultValues() = runBlocking {
        database.categoryDao().insert(sampleCategory1)
        database.brandDao().insert(sampleBrand1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click trains from the bottom menu
        onView(withId(R.id.trains)).perform(click())

        //Click add button from action menu
        onView(withId(R.id.action_add)).perform(click())

        //Verify AddTrainFragment is launched with empty or default fields
        onView(withText(R.string.add_train)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withText("--Select category--")).check(matches(isDisplayed()))
        onView(withText("--Select brand--")).check(matches(isDisplayed()))
        onView(withId(R.id.editReference)).check(matches(withText("")))
        onView(withId(R.id.editTrainName)).check(matches(withText("")))
        onView(withId(R.id.editScale)).check(matches(withText("")))
        onView(withId(R.id.editLocation)).check(matches(withText("")))
        onView(withId(R.id.editTrainDescription)).check(matches(withText("")))
        //Verify bottom navigation is gone
        onView(withId(R.id.navigation)).check(isGone())
        //Verify up button is seen instead of hamburger icon
        onView(withContentDescription(R.string.abc_action_bar_up_description)).check(matches(isDisplayed()))

        //Fill in the widgets
        onView(withId(R.id.categorySpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(sampleTrain1.categoryName))).perform(click())
        onView(withId(R.id.brandSpinner)).perform(click())
        onData(allOf(`is`(instanceOf(BrandEntry::class.java)))).perform(click())
        onView(withId(R.id.editReference)).perform(typeText(sampleTrain1.modelReference), closeSoftKeyboard())
        onView(withId(R.id.editTrainName)).perform(scrollTo(), typeText(sampleTrain1.trainName), closeSoftKeyboard())
        onView(withId(R.id.editScale)).perform(scrollTo(), click(), typeText(sampleTrain1.scale), closeSoftKeyboard())
        onView(withId(R.id.editLocation)).perform(scrollTo(), typeText(sampleTrain1.location), closeSoftKeyboard())
        onView(withId(R.id.editTrainDescription)).perform(scrollTo(), typeText(sampleTrain1.description), closeSoftKeyboard())

        //Click save button from action menu
        onView(withId(R.id.action_save)).perform(click())

        onView(withText(R.string.all_trains)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.navigation)).check(isVisible())
        onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun showTrainDetails_deleteItem() = runBlocking {
        database.categoryDao().insert(sampleCategory1)
        database.brandDao().insert(sampleBrand1)
        database.trainDao().insert(sampleTrain1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click trains from the bottom menu
        onView(withId(R.id.trains)).perform(click())

        //Click on the train item
        onView(withId(R.id.list))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        //Verify details frag is launched and show correct data
        onView(withText(sampleTrain1.trainName)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.details_brand)).check(matches(withText(sampleTrain1.brandName)))
        onView(withId(R.id.details_category)).check(matches(withText(sampleTrain1.categoryName)))
        onView(withId(R.id.details_reference)).check(matches(withText(sampleTrain1.modelReference)))
        onView(withId(R.id.details_scale)).check(matches(withText(sampleTrain1.scale)))
        onView(withId(R.id.details_description)).check(matches(withText(sampleTrain1.description)))
        onView(withId(R.id.details_location)).check(matches(withText(sampleTrain1.location)))

        //Click delete
        onView(withId(R.id.action_delete)).perform(click())
        //Verify a warning dialog is shown
        onView(withText(R.string.do_you_want_to_delete)).check(matches(isDisplayed()))
        //Confirm delete
        onView(withText(R.string.yes_delete)).perform(click())
        //Verify we are back at TrainListFragment and the train is deleted
        onView(withText(R.string.all_trains)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.empty_image)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editTrain_clickBack_warnOnlyIfModified(): Unit = runBlocking {
        database.categoryDao().insert(sampleCategory1)
        database.brandDao().insert(sampleBrand1)
        database.trainDao().insert(sampleTrain1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click trains from the bottom menu
        onView(withId(R.id.trains)).perform(click())

        //Click on the train item
        onView(withId(R.id.list)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        //Click edit button
        onView(withId(R.id.action_edit)).perform(click())
        //Verify we are on AddTrainFragment, in edit mode, with correct data set on edittexts
        onView(withText(R.string.edit_train)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.editTrainName)).check(matches(withText(sampleTrain1.trainName)))
        onView(withText(sampleTrain1.categoryName)).check(matches(isDisplayed()))
        onView(withText(sampleTrain1.brandName)).check(matches(isDisplayed()))

        Espresso.pressBack()
        //Verify we are back at details screen without a warning
        onView(withText(sampleTrain1.trainName)).check(matches(withParent(withId(R.id.toolbar))))
        //Click edit button again
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.editTrainName)).perform(replaceText("modified train name"))

        Espresso.pressBack()
        onView(withText(R.string.unsaved_changes_warning)).check(matches(isDisplayed()))

        activityScenario.close()

    }

    @Test
    fun editTrain_clickUp_warnOnlyIfModified(): Unit = runBlocking {
        database.categoryDao().insert(sampleCategory1)
        database.brandDao().insert(sampleBrand1)
        database.trainDao().insert(sampleTrain1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click trains from the bottom menu
        onView(withId(R.id.trains)).perform(click())

        //Click on the train item
        onView(withId(R.id.list)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        //Click edit button
        onView(withId(R.id.action_edit)).perform(click())
        //Verify we are on AddTrainFragment, in edit mode, with correct data set on edittexts
        onView(withText(R.string.edit_train)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.editTrainName)).check(matches(withText(sampleTrain1.trainName)))
        onView(withText(sampleTrain1.categoryName)).check(matches(isDisplayed()))
        onView(withText(sampleTrain1.brandName)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        //Verify we are back at details screen without a warning
        onView(withText(sampleTrain1.trainName)).check(matches(withParent(withId(R.id.toolbar))))
        //Click edit button again
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.editTrainName)).perform(replaceText("modified train name"))

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withText(R.string.unsaved_changes_warning)).check(matches(isDisplayed()))

        activityScenario.close()

    }

    @Test
    fun searchATrain_correctResultsShown() = runBlocking {
        database.categoryDao().insert(sampleCategory1)
        database.brandDao().insert(sampleBrand1)
        database.trainDao().insert(sampleTrain1)
        database.trainDao().insert(sampleTrain3)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click trains from the bottom menu
        onView(withId(R.id.trains)).perform(click())

        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(typeText("red"), pressImeActionButton())

        onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))
        onView(withText(sampleTrain3.trainName)).check(doesNotExist())

        activityScenario.close()
    }

}