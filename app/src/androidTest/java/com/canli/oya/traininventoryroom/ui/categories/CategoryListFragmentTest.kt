package com.canli.oya.traininventoryroom.ui.categories

import android.os.Bundle
import android.view.MenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.source.FakeCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ICategoryDataSource
import com.canli.oya.traininventoryroom.di.AndroidTestApplication
import com.canli.oya.traininventoryroom.di.TestComponent
import com.canli.oya.traininventoryroom.utils.clickOnChildWithId
import com.canli.oya.traininventoryroom.utils.isGone
import com.canli.oya.traininventoryroom.utils.isVisible
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
class CategoryListFragmentTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: ICategoryDataSource

    val sampleCategory1 = CategoryEntry(0, "Wagon")
    val sampleCategory2 = CategoryEntry(1, "Locomotive")
    val sampleCategoryList = mutableListOf(sampleCategory1, sampleCategory2)

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
            (dataSource as FakeCategoryDataSource).setData(mutableListOf())
            launchFragmentInContainer<CategoryListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.empty_text)).check(isVisible())
            onView(withId(R.id.empty_image)).check(isVisible())
        }
    }

    //Launch with a sample list. Verify that empty layout is not shown and the list is shown with correct items
    @Test
    fun withSampleList_emptyScreenIsNotShown() {
        runBlockingTest {
            //Set some sample data
            (dataSource as FakeCategoryDataSource).setData(sampleCategoryList)
            launchFragmentInContainer<CategoryListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.empty_text)).check(isGone())
            onView(withId(R.id.empty_image)).check(isGone())
        }
    }

    @Test
    fun withSampleList_ListIsShown() {
        runBlockingTest {
            //Set some sample data
            (dataSource as FakeCategoryDataSource).setData(sampleCategoryList)
            launchFragmentInContainer<CategoryListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.list)).check(isVisible())
        }
    }

    //Click plus on the menu and verify that add child frag becomes visible with empty edittext
    @Test
    fun clickAdd_opensEmptyAddFragment() {
        runBlockingTest {
            (dataSource as FakeCategoryDataSource).setData(sampleCategoryList)
            val scenario = launchFragmentInContainer<CategoryListFragment>(Bundle(), R.style.AppTheme)

            val addMenuItem = mock(MenuItem::class.java)
            `when`(addMenuItem.itemId).thenReturn(R.id.action_add)
            //Click on the add menu item
            scenario.onFragment { fragment ->
                fragment.onOptionsItemSelected(addMenuItem)
            }

            //Check whether add category screen becomes visible
            onView(withId(R.id.addCategory_editCatName)).check(matches(isDisplayed()))
            onView(withId(R.id.addCategory_editCatName)).check(matches(withText("")))
            onView(withId(R.id.addCategory_saveBtn)).check(matches(isDisplayed()))

        }
    }


    //Click edit on a category and verify that add child frag becomes visible with the category name inside edittext
    @Test
    fun clickEditONItem_opensAddFragmentFilled() {
        runBlockingTest {
            (dataSource as FakeCategoryDataSource).setData(sampleCategoryList)
            launchFragmentInContainer<CategoryListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.list))
                    .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, clickOnChildWithId(R.id.category_item_edit_icon)))

            onView(withId(R.id.addCategory_editCatName)).check(matches(isDisplayed()))
            onView(withId(R.id.addCategory_editCatName)).check(matches(withText("Locomotive")))
            onView(withId(R.id.addCategory_saveBtn)).check(matches(isDisplayed()))
        }
    }
}