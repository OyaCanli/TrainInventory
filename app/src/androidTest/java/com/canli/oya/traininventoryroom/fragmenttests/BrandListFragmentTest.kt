package com.canli.oya.traininventoryroom.fragmenttests

import android.content.Context
import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.di.DataSourceModule
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.utils.clickOnChildWithId
import com.canli.oya.traininventoryroom.utils.isGone
import com.canli.oya.traininventoryroom.utils.isVisible
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.local.TrainDatabase
import com.canlioya.testresources.datasource.FakeBrandDataSource
import com.canlioya.testresources.datasource.FakeCategoryDataSource
import com.canlioya.testresources.datasource.FakeTrainDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.fragment.testing.HiltFragmentScenario
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.mockito.Mockito
import javax.inject.Inject

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
@RunWith(AndroidJUnit4::class)
class BrandListFragmentTest {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeDataModule {

        @Provides
        fun provideTrainDataSource(): ITrainDataSource = FakeTrainDataSource()

        @Provides
        fun provideCategoryDataSource(): IBrandCategoryDataSource<Category> = FakeCategoryDataSource()

        @Provides
        fun provideBrandDataSource(): IBrandCategoryDataSource<Brand> = FakeBrandDataSource()

        @Provides
        fun provideDatabase(): TrainDatabase = Mockito.mock(TrainDatabase::class.java)

        @IODispatcher
        @Provides
        fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
    }

    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var rule = RuleChain.outerRule(hiltRule)
        .around(InstantTaskExecutorRule())

    @Inject
    lateinit var dataSource: IBrandCategoryDataSource<Brand>

    @Before
    fun init() {
        hiltRule.inject()
    }

    // Launch with a sample list. Verify that empty layout is not shown and the list is shown with correct items
    @Test
    fun withSampleList_emptyScreenIsNotShown() {
        runBlockingTest {
            HiltFragmentScenario.launchInContainer(BrandListFragment::class.java, Bundle())

            onView(withId(R.id.empty_text)).check(isGone())
            onView(withId(R.id.empty_image)).check(isGone())
            onView(withId(R.id.list)).check(isVisible())
        }
    }

    @Test
    fun clickAdd_opensEmptyAddFragment() {
        runBlockingTest {
            val scenario = HiltFragmentScenario.launchInContainer(BrandListFragment::class.java, Bundle())

            val context: Context = ApplicationProvider.getApplicationContext<TrainApplication>()
            val addMenuItem = ActionMenuItem(context, 0, R.id.action_add, 0, 0, null)
            // Click on the add menu item
            scenario.onFragment { fragment ->
                fragment.onOptionsItemSelected(addMenuItem)
            }

            // Check whether add category screen becomes visible
            onView(withId(R.id.addBrand_editBrandName)).check(matches(isDisplayed()))
            onView(withId(R.id.addBrand_editBrandName)).check(matches(withText("")))
            onView(withId(R.id.addBrandLogoBtn)).check(matches(withText(R.string.add_logo)))
            onView(withId(R.id.addBrand_saveBtn)).check(matches(isDisplayed()))
        }
    }

    // Click edit on a category and verify that add child frag becomes visible with the category name inside edittext
    @Test
    fun clickEditOnItem_opensAddFragmentFilled() {
        runBlockingTest {
            HiltFragmentScenario.launchInContainer(BrandListFragment::class.java, Bundle())

            onView(withId(R.id.list))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, clickOnChildWithId(R.id.brand_item_edit_icon)))

            // Check whether add category screen becomes visible
            onView(withId(R.id.addBrand_editBrandName)).check(matches(isDisplayed()))
            onView(withId(R.id.addBrand_editBrandName)).check(matches(withText("MDN")))
            onView(withId(R.id.addBrandLogoBtn)).check(matches(withText(R.string.add_logo)))
            onView(withId(R.id.addBrand_saveBtn)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun swipingItem_revealsDeleteConfirmation() {
        runBlockingTest {
            HiltFragmentScenario.launchInContainer(BrandListFragment::class.java, Bundle())

            // Swipe an item
            onView(withId(R.id.list))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, swipeLeft()))

            // Verify that delete confirmation layout is displayed
            onView(withId(R.id.confirm_delete_btn)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.cancel_btn)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withText(R.string.do_you_want_to_delete)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }
}
