package com.canli.oya.traininventoryroom.fragmenttests

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.datasource.sampleTrain2
import com.canli.oya.traininventoryroom.datasource.sampleTrainList
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.fake.DaggerFakeTestComponent
import com.canli.oya.traininventoryroom.di.fake.FakeTestComponent
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import com.canli.oya.traininventoryroom.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TrainListFragmentTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: ITrainDataSource

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
        (ComponentProvider.getInstance(app).daggerComponent as FakeTestComponent).inject(this)

        //Set some sample data
        (dataSource as FakeTrainDataSource).setData(sampleTrainList)
    }

    //Launch with a sample list. Verify that empty layout is not shown and the list is shown with correct items
    @Test
    fun allTrains_withSampleList_emptyScreenIsNotShown() {
        runBlockingTest {
            //Launch the fragment in ALL_TRAINS mode
            val args = Bundle()
            args.putString(INTENT_REQUEST_CODE, ALL_TRAIN)
            val fragmentScenario = launchFragmentInContainer<TrainListFragment>(args, R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Verify that empty layout is not displayed and that the list is displayed
            onView(withId(R.id.empty_text)).check(isGone())
            onView(withId(R.id.empty_image)).check(isGone())
            onView(withId(R.id.list)).check(isVisible())
        }
    }

    @Test
    fun trainsOfBrand_withNoResult_showsEmptyScreen() {
        runBlockingTest {
            //Launches the fragment in TRAINS_OF_BRAND mode
            val args = Bundle()
            args.putString(INTENT_REQUEST_CODE, TRAINS_OF_BRAND)
            args.putString(BRAND_NAME, "unexisting brand")
            val fragmentScenario = launchFragmentInContainer<TrainListFragment>(args, R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Check if empty layout is displayed
            onView(withId(R.id.empty_text)).check(isVisible())
            onView(withId(R.id.empty_image)).check(isVisible())
        }
    }

    @Test
    fun trainsOfBrand_withAResult_showsCorrectResult() {
        runBlockingTest {
            //Launches the fragment in TRAINS_OF_BRAND mode
            val sampleBrandName = sampleTrain1.brandName
            val args = Bundle()
            args.putString(INTENT_REQUEST_CODE, TRAINS_OF_BRAND)
            args.putString(BRAND_NAME, sampleBrandName)
            val fragmentScenario = launchFragmentInContainer<TrainListFragment>(args, R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            onView(withId(R.id.list)).check(isVisible())
            onView(withText(sampleTrain1.trainName)).check(isVisible())
        }
    }

    @Test
    fun trainsOfCategory_withNoResult_showsEmptyScreen() {
        runBlockingTest {
            //Launch the fragment in TRAINS_OF_CATEGORY mode
            val args = Bundle()
            args.putString(INTENT_REQUEST_CODE, TRAINS_OF_CATEGORY)
            args.putString(CATEGORY_NAME, "Unexisting Category")
            val fragmentScenario = launchFragmentInContainer<TrainListFragment>(args, R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Check if empty layout is displayed
            onView(withId(R.id.empty_text)).check(isVisible())
            onView(withId(R.id.empty_image)).check(isVisible())
        }
    }

    @Test
    fun trainsOfCategory_withAResult_showsCorrectResult() {
        runBlockingTest {
            //Launch the fragment in TRAINS_OF_CATEGORY mode
            val sampleCategoryName = sampleTrain2.categoryName
            val args = Bundle()
            args.putString(INTENT_REQUEST_CODE, TRAINS_OF_CATEGORY)
            args.putString(CATEGORY_NAME, sampleCategoryName)
            val fragmentScenario = launchFragmentInContainer<TrainListFragment>(args, R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            val enclosedInParenthesis = "($sampleCategoryName)"
            onView(withId(R.id.list)).check(isVisible())
            onView(withText(enclosedInParenthesis)).check(isVisible())
        }
    }

    @Test
    fun swipingItem_revealsDeleteConfirmation() {
        runBlockingTest {
            //Launch the fragment in ALL_TRAINS mode
            val args = Bundle()
            args.putString(INTENT_REQUEST_CODE, ALL_TRAIN)
            val fragmentScenario = launchFragmentInContainer<TrainListFragment>(args, R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Swipe an item
            onView(withId(R.id.list))
                    .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, swipeLeft()))

            //Verify that delete confirmation layout is displayed
            onView(withId(R.id.confirm_delete_btn)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.cancel_btn)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withText(R.string.do_you_want_to_delete)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }
}