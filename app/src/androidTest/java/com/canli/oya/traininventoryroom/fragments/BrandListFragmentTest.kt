package com.canli.oya.traininventoryroom.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.source.FakeBrandDataSource
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.sampleBrandList
import com.canli.oya.traininventoryroom.di.AndroidTestApplication
import com.canli.oya.traininventoryroom.di.TestComponent
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class BrandListFragmentTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: IBrandCategoryDataSource<BrandEntry>

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
        val app = ApplicationProvider.getApplicationContext<AndroidTestApplication>()
        val component = app.appComponent as TestComponent
        component.inject(this)
    }

    //Launch with an empty list. Verify that empty text and image are shown and verify that the list is not shown
    @Test
    fun withEmptyList_emptyScreenIsShown() {
        runBlockingTest {
            (dataSource as FakeBrandDataSource).setData(mutableListOf())
            val fragmentScenario = launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            onView(withId(R.id.empty_text)).check(isVisible())
            onView(withId(R.id.empty_image)).check(isVisible())
            onView(withId(R.id.list)).check(isGone())
        }
    }

    //Launch with a sample list. Verify that empty layout is not shown and the list is shown with correct items
    @Test
    fun withSampleList_emptyScreenIsNotShown() {
        runBlockingTest {
            //Set some sample data
            (dataSource as FakeBrandDataSource).setData(sampleBrandList)
            val fragmentScenario = launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            onView(withId(R.id.empty_text)).check(isGone())
            onView(withId(R.id.empty_image)).check(isGone())
            onView(withId(R.id.list)).check(isVisible())
        }
    }

    @Test
    fun clickAdd_opensEmptyAddFragment() {
        runBlockingTest {
            (dataSource as FakeBrandDataSource).setData(sampleBrandList)
            val scenario = launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(scenario)

            val context: Context = ApplicationProvider.getApplicationContext<AndroidTestApplication>()
            val addMenuItem = ActionMenuItem(context, 0, R.id.action_add, 0, 0, null)
            //Click on the add menu item
            scenario.onFragment { fragment ->
                fragment.onOptionsItemSelected(addMenuItem)
            }

            //Check whether add category screen becomes visible
            onView(withId(R.id.addBrand_editBrandName)).check(matches(isDisplayed()))
            onView(withId(R.id.addBrand_editBrandName)).check(matches(withText("")))
            onView(withId(R.id.addBrand_image)).check(matches(isDisplayed()))
            onView(withId(R.id.addBrand_saveBtn)).check(matches(isDisplayed()))
        }
    }

    //Click edit on a category and verify that add child frag becomes visible with the category name inside edittext
    @Test
    fun clickEditOnItem_opensAddFragmentFilled() {
        runBlockingTest {
            (dataSource as FakeBrandDataSource).setData(sampleBrandList)
            val fragmentScenario = launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            onView(withId(R.id.list))
                    .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, clickOnChildWithId(R.id.brand_item_edit_icon)))

            //Check whether add category screen becomes visible
            onView(withId(R.id.addBrand_editBrandName)).check(matches(isDisplayed()))
            onView(withId(R.id.addBrand_editBrandName)).check(matches(withText("MDN")))
            onView(withId(R.id.addBrand_image)).check(matches(isDisplayed()))
            onView(withId(R.id.addBrand_saveBtn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun swipingItem_revealsDeleteConfirmation() {
        runBlockingTest {
            //Set some sample data
            (dataSource as FakeBrandDataSource).setData(sampleBrandList)

            val fragmentScenario = launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Swipe an item
            onView(withId(R.id.list))
                    .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, swipeLeft()))

            //Verify that delete confirmation layout is displayed
            onView(withId(R.id.confirm_delete_btn)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.cancel_btn)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withText(R.string.do_you_want_to_delete)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }
}