package com.canli.oya.traininventoryroom.fragmenttests

import android.os.Bundle
import android.view.View
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.fake.DaggerFakeTestComponent
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.utils.CHOSEN_TRAIN
import com.canli.oya.traininventoryroom.utils.DataBindingIdlingResource
import com.canli.oya.traininventoryroom.utils.IS_EDIT
import com.canli.oya.traininventoryroom.utils.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddTrainFragmentTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

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

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<TrainApplication>()
        ComponentProvider.getInstance(app).daggerComponent = DaggerFakeTestComponent.builder()
                .testAppModule(TestAppModule(app))
                .build()
    }

    //Launch frag in add mode. Verify that all widgets are empty or have default values
    @Test
    fun inAddMode_allWidgetsShowEmptyOrDefaultValues() = runBlockingTest {
        //Launch fragment in add mode
        val args = Bundle()
        args.putBoolean(IS_EDIT, false)
        val fragmentScenario = launchFragmentInContainer<AddTrainFragment>(args, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        onView(withText("--Select category--")).check(matches(isDisplayed()))
        onView(withText("--Select brand--")).check(matches(isDisplayed()))
        onView(withId(R.id.editReference)).check(matches(withText("")))
        onView(withId(R.id.editTrainName)).check(matches(withText("")))
        onView(withId(R.id.editScale)).check(matches(withText("")))
        onView(withId(R.id.editLocation)).check(matches(withText("")))
        onView(withId(R.id.editTrainDescription)).check(matches(withText("")))

    }

    @Test
    fun inEditMode_widgetsShowCorrectValues() = runBlockingTest {
        //Launch fragment in add mode
        val args = Bundle()
        args.putBoolean(IS_EDIT, true)
        args.putParcelable(CHOSEN_TRAIN, sampleTrain1)
        val fragmentScenario = launchFragmentInContainer<AddTrainFragment>(args, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        onView(withText(sampleTrain1.brandName)).check(matches(isDisplayed()))
        onView(withText(sampleTrain1.categoryName)).check(matches(isDisplayed()))
        onView(withId(R.id.editReference)).check(matches(withText(sampleTrain1.modelReference)))
        onView(withId(R.id.editTrainName)).check(matches(withText(sampleTrain1.trainName)))
        onView(withId(R.id.editScale)).check(matches(withText(sampleTrain1.scale)))
        onView(withId(R.id.editLocation)).check(matches(withText(sampleTrain1.location)))
        onView(withId(R.id.editTrainDescription)).check(matches(withText(sampleTrain1.description)))

    }

    @Test
    fun inAddMode_saveWithoutCategory_showsAToast() = runBlockingTest {
        //Launch fragment in add mode
        val args = Bundle()
        args.putBoolean(IS_EDIT, false)
        val fragmentScenario = launchFragmentInContainer<AddTrainFragment>(args, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        //Click on save menu item
        val saveMenuItem = ActionMenuItem(null, 0, R.id.action_save, 0, 0, null)
        var decorView : View? = null
        fragmentScenario.onFragment { fragment ->
            decorView = fragment.activity?.window?.decorView
            fragment.onOptionsItemSelected(saveMenuItem)
        }

        //Verify a toast is shown because category is not chosen
        onView(withText(R.string.category_name_empty)).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun inAddMode_saveWithoutBrand_showsAToast() = runBlockingTest {
        //Launch fragment in add mode
        val args = Bundle()
        args.putBoolean(IS_EDIT, false)
        val fragmentScenario = launchFragmentInContainer<AddTrainFragment>(args, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        onView(withId(R.id.categorySpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(sampleTrain1.categoryName))).perform(click())

        //Click on save menu item
        val saveMenuItem = ActionMenuItem(null, 0, R.id.action_save, 0, 0, null)
        var decorView : View? = null
        fragmentScenario.onFragment { fragment ->
            decorView = fragment.activity?.window?.decorView
            fragment.onOptionsItemSelected(saveMenuItem)
        }

        //Verify a toast is shown since brand name is not chosen
        onView(withText(R.string.brand_cannot_be_empty)).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun inAddMode_saveWithoutTrainName_showsAToast() = runBlockingTest {
        //Launch fragment in add mode
        val args = Bundle()
        args.putBoolean(IS_EDIT, false)
        val fragmentScenario = launchFragmentInContainer<AddTrainFragment>(args, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        onView(withId(R.id.categorySpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(sampleTrain1.categoryName))).perform(click())
        onView(withId(R.id.brandSpinner)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        //Click on save menu item
        val saveMenuItem = ActionMenuItem(null, 0, R.id.action_save, 0, 0, null)
        var decorView : View? = null
        fragmentScenario.onFragment { fragment ->
            decorView = fragment.activity?.window?.decorView
            fragment.onOptionsItemSelected(saveMenuItem)
        }

        //Verify a toast is shown since train name is empty
        onView(withText(R.string.train_name_empty)).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()))

    }


    /*Launch in add mode. Add something and click back. Verify that a dialog is shown. */
    @Test
    fun inAddMode_insertDataAndClickBack_showsWarningDialog() = runBlockingTest {
        //Launch fragment in add mode
        val args = Bundle()
        args.putBoolean(IS_EDIT, false)
        val fragmentScenario = launchFragmentInContainer<AddTrainFragment>(args, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        //Type something in a field
        onView(withId(R.id.editTrainName)).perform(scrollTo(), typeText(sampleTrain1.trainName), closeSoftKeyboard())

        fragmentScenario.onFragment { fragment ->
            fragment.onBackClicked()
        }

        onView(withText(R.string.unsaved_changes_warning)).check(matches(isDisplayed()))

    }

    @Test
    fun inEditMode_changeDataAndClickBack_showsWarningDialog() = runBlockingTest {
        //Launch fragment in add mode
        val args = Bundle()
        args.putBoolean(IS_EDIT, true)
        args.putParcelable(CHOSEN_TRAIN, sampleTrain1)
        val fragmentScenario = launchFragmentInContainer<AddTrainFragment>(args, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        //Type something in a field
        onView(withId(R.id.editTrainName)).perform(scrollTo(), replaceText("changed train name"), closeSoftKeyboard())

        fragmentScenario.onFragment { fragment ->
            fragment.onBackClicked()
        }

        onView(withText(R.string.unsaved_changes_warning)).check(matches(isDisplayed()))
    }
}
