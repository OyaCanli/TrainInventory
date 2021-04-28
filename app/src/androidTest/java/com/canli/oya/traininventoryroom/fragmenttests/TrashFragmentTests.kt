package com.canli.oya.traininventoryroom.fragmenttests

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
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
import com.canli.oya.traininventoryroom.ui.trash.TrashListFragment
import com.canli.oya.traininventoryroom.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import javax.inject.Inject

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TrashFragmentTests {

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

    @Test
    fun whenNoTrash_showsEmptyMessage() {
        runBlockingTest {
            //Launch trash fragment with no trash
            val fragmentScenario = launchFragmentInContainer<TrashListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Verify that empty layout is displayed and that the list is not displayed
            onView(withId(R.id.empty_text)).check(isVisible())
            onView(withId(R.id.list)).check(isGone())
            onView(withText(R.string.no_trains_in_trash)).check(isVisible())
        }
    }

    @Test
    fun whenThereIsTrash_showsIt() {
        runBlockingTest {
            //Launch with an item on trash
            val date = LocalDate.now().toEpochDay()
            dataSource.sendTrainToTrash(sampleTrain1.trainId, date)
            val fragmentScenario = launchFragmentInContainer<TrashListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Verify that empty layout is displayed and that the list is not displayed
            onView(withId(R.id.empty_text)).check(isGone())
            onView(withId(R.id.empty_image)).check(isGone())
            onView(withId(R.id.list)).check(isVisible())
            onView(withText(sampleTrain1.trainName)).check(isVisible())
        }
    }

    /*@Test
    fun clickOnRestore_removesItFromTrash() {
        runBlockingTest {
            //Launch with an item on trash
            val date = LocalDate.now().toEpochDay()
            dataSource.sendTrainToTrash(sampleTrain1.trainId, date)
            val fragmentScenario = launchFragmentInContainer<TrashListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Click on restore on item
            onView(withId(R.id.list)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.trash_item_restore)))

            onView(withId(R.id.empty_image)).check(matches(isDisplayed()))
        }
    }*/

    @Test
    fun clickOnDeleteForever_showsWarningDialog() {
        runBlockingTest {
            //Launch with an item on trash
            val date = LocalDate.now().toEpochDay()
            dataSource.sendTrainToTrash(sampleTrain1.trainId, date)
            val fragmentScenario = launchFragmentInContainer<TrashListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Click on restore on item
            onView(withId(R.id.list)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.trash_item_delete)))
            onView(withText(R.string.do_you_want_to_permanently_delete_train)).check(matches(isDisplayed()))
        }
    }
}