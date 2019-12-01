package com.canli.oya.traininventoryroom.ui.brands

import android.os.Bundle
import android.view.MenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.source.FakeBrandDataSource
import com.canli.oya.traininventoryroom.data.source.IBrandDataSource
import com.canli.oya.traininventoryroom.di.AndroidTestApplication
import com.canli.oya.traininventoryroom.di.TestComponent
import com.canli.oya.traininventoryroom.utils.clickOnChildWithId
import com.canli.oya.traininventoryroom.utils.isGone
import com.canli.oya.traininventoryroom.utils.isVisible
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import javax.inject.Inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class BrandListFragmentTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: IBrandDataSource

    val sampleBrand1 = BrandEntry(0, "Markin")
    val sampleBrand2 = BrandEntry(1, "MDN")
    val sampleBrand3 = BrandEntry(2, "Legit")
    val sampleBrandList = mutableListOf(sampleBrand1, sampleBrand2)

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<AndroidTestApplication>()
        val component = app.appComponent as TestComponent
        component.inject(this@BrandListFragmentTest)
    }

    //Launch with an empty list. Verify that empty text and image are shown and verify that the list is not shown
    @Test
    fun withEmptyList_emptyScreenIsShown() {
        runBlockingTest {
            (dataSource as FakeBrandDataSource).setData(mutableListOf())
            launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.empty_text)).check(isVisible())
            onView(withId(R.id.empty_image)).check(isVisible())
        }
    }

    //Launch with a sample list. Verify that empty layout is not shown and the list is shown with correct items
    @Test
    fun withSampleList_emptyScreenIsNotShown() {
        runBlockingTest {
            //Set some sample data
            (dataSource as FakeBrandDataSource).setData(sampleBrandList)
            launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.empty_text)).check(isGone())
            onView(withId(R.id.empty_image)).check(isGone())
        }
    }

    @Test
    fun withSampleList_listIsShown() {
        runBlockingTest {
            //Set some sample data
            (dataSource as FakeBrandDataSource).setData(sampleBrandList)
            launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.list)).check(isVisible())
        }
    }

    @Test
    fun clickAdd_opensEmptyAddFragment() {
        runBlockingTest {
            (dataSource as FakeBrandDataSource).setData(sampleBrandList)
            val scenario = launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)

            val addMenuItem = Mockito.mock(MenuItem::class.java)
            Mockito.`when`(addMenuItem.itemId).thenReturn(R.id.action_add)
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
            launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.list))
                    .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, clickOnChildWithId(R.id.brand_item_edit_icon)))

            //Check whether add category screen becomes visible
            onView(withId(R.id.addBrand_editBrandName)).check(matches(isDisplayed()))
            onView(withId(R.id.addBrand_editBrandName)).check(matches(withText("MDN")))
            onView(withId(R.id.addBrand_image)).check(matches(isDisplayed()))
            onView(withId(R.id.addBrand_saveBtn)).check(matches(isDisplayed()))
        }
    }
}