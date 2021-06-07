package com.canli.oya.traininventoryroom.fragmenttests

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntity
import com.canli.oya.traininventoryroom.data.CategoryEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.datasource.FakeBrandDataSource
import com.canli.oya.traininventoryroom.datasource.FakeCategoryDataSource
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.fake.DaggerFakeTestComponent
import com.canli.oya.traininventoryroom.di.fake.FakeTestComponent
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
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
class EmptyScreenTests {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var categoryDataSource: IBrandCategoryDataSource<CategoryEntity>
    @Inject
    lateinit var brandDataSource: IBrandCategoryDataSource<BrandEntity>
    @Inject
    lateinit var trainDataSource: ITrainDataSource

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

        //Set an empty list as data
        (trainDataSource as FakeTrainDataSource).setData(mutableListOf())
        (brandDataSource as FakeBrandDataSource).setData(mutableListOf())
        (categoryDataSource as FakeCategoryDataSource).setData(mutableListOf())

    }

    //Launch with an empty list. Verify that empty text and image are shown and verify that the list is not shown
    @Test
    fun brandList_withEmptyList_emptyScreenIsShown() {
        runBlockingTest {
            val fragmentScenario = launchFragmentInContainer<BrandListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            Espresso.onView(ViewMatchers.withId(R.id.empty_text)).check(isVisible())
            Espresso.onView(ViewMatchers.withId(R.id.empty_image)).check(isVisible())
            Espresso.onView(ViewMatchers.withId(R.id.list)).check(isGone())
        }
    }

    //Launch with an empty list. Verify that empty text and image are shown and verify that the list is not shown
    @Test
    fun categoryList_withEmptyList_emptyScreenIsShown() {
        runBlockingTest {
            val fragmentScenario = launchFragmentInContainer<CategoryListFragment>(Bundle(), R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            Espresso.onView(ViewMatchers.withId(R.id.empty_text)).check(isVisible())
            Espresso.onView(ViewMatchers.withId(R.id.empty_image)).check(isVisible())
            Espresso.onView(ViewMatchers.withId(R.id.list)).check(isGone())
        }
    }

    //Launch with an empty list. Verify that empty text and image are shown and verify that the list is not shown
    @Test
    fun allTrains_withEmptyList_emptyScreenIsShown() {
        runBlockingTest {
            //Launch the fragment in All Trains mode
            val args = Bundle()
            args.putString(INTENT_REQUEST_CODE, ALL_TRAIN)
            val fragmentScenario = launchFragmentInContainer<TrainListFragment>(args, R.style.AppTheme)
            dataBindingIdlingResource.monitorFragment(fragmentScenario)

            //Check if empty layout is displayed
            Espresso.onView(ViewMatchers.withId(R.id.empty_text)).check(isVisible())
            Espresso.onView(ViewMatchers.withId(R.id.empty_image)).check(isVisible())
        }
    }
}