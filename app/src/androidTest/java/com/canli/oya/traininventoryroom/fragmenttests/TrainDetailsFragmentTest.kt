package com.canli.oya.traininventoryroom.fragmenttests


import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.datasource.sampleTrainList
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.fake.DaggerFakeTestComponent
import com.canli.oya.traininventoryroom.di.fake.FakeTestComponent
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragment
import com.canli.oya.traininventoryroom.utils.DataBindingIdlingResource
import com.canli.oya.traininventoryroom.utils.TRAIN_ID
import com.canli.oya.traininventoryroom.utils.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TrainDetailsFragmentTest{

    private lateinit var scenario: FragmentScenario<TrainDetailsFragment>

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

        val args = Bundle()
        args.putInt(TRAIN_ID, 0)
        scenario = launchFragmentInContainer<TrainDetailsFragment>(args, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
    }

    //Verify chosen train is displayed
    @Test
    fun verifyWidgetsArePopulatedWithChosenTrain() {
        onView(withId(R.id.details_reference)).check(matches(withText(sampleTrain1.modelReference)))
        onView(withId(R.id.details_brand)).check(matches(withText(sampleTrain1.brandName)))
        onView(withId(R.id.details_category)).check(matches(withText(sampleTrain1.categoryName)))
        onView(withId(R.id.details_description)).check(matches(withText(sampleTrain1.description)))
        onView(withId(R.id.details_quantity)).check(matches(withText(sampleTrain1.quantity.toString())))
        onView(withId(R.id.details_scale)).check(matches(withText(sampleTrain1.scale)))
        onView(withId(R.id.details_location)).check(matches(withText("2-A")))
    }

    //Click on delete menu item and verify that a dialog shows up
    @Test
    fun clickDeleteMenuItem_launchesAWarningDialog() {
        //Click on delete menu item
        val deleteMenuItem = ActionMenuItem(null, 0, R.id.action_delete, 0, 0, null)
        //Click on the edit menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(deleteMenuItem)
        }

        onView(withText(R.string.do_you_want_to_delete)).check(matches(isDisplayed()))
    }
}