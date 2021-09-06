package com.canli.oya.traininventoryroom.fragmenttests

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.di.DataSourceModule
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.ui.filter.FilterTrainFragment
import com.canli.oya.traininventoryroom.utils.*
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.testresources.datasource.*
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
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.mockito.Mockito

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
@RunWith(AndroidJUnit4::class)
class FilterTrainFragmentTests {

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
        fun provideDatabase(): com.canlioya.local.TrainDatabase = Mockito.mock(com.canlioya.local.TrainDatabase::class.java)

        @IODispatcher
        @Provides
        fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
    }

    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var rule = RuleChain.outerRule(hiltRule)
        .around(InstantTaskExecutorRule())

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun trainsOfBrand_withNoResult_showsEmptyScreen() = runBlockingTest {
        // Launches the fragment in TRAINS_OF_BRAND mode
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_BRAND)
        args.putString(BRAND_NAME, sampleBrand3.brandName)
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, args)

        // Check if empty layout is displayed
        onView(withId(R.id.empty_text)).check(isVisible())
        onView(withId(R.id.list)).check(isGone())
    }

    @Test
    fun trainsOfBrand_withAResult_showsCorrectResult() = runBlockingTest {
        // Launches the fragment in TRAINS_OF_BRAND mode
        val sampleBrandName = sampleTrain1.brandName
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_BRAND)
        args.putString(BRAND_NAME, sampleBrandName)
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, args)

        onView(withId(R.id.empty_text)).check(isGone())
        onView(withId(R.id.empty_image)).check(isGone())
        onView(withId(R.id.list)).check(isVisible())
        onView(withText(sampleTrain1.trainName)).check(isVisible())
    }

    @Test
    fun trainsOfCategory_withNoResult_showsEmptyScreen() = runBlockingTest {
        // Launch the fragment in TRAINS_OF_CATEGORY mode
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_CATEGORY)
        args.putString(CATEGORY_NAME, sampleCategory3.categoryName)
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, args)

        // Check category passed is shown as selected on the spinner
        onView(withText(sampleCategory3.categoryName)).check(matches(isDisplayed()))

        // Check if empty layout is displayed
        onView(withId(R.id.empty_text)).check(isVisible())
        onView(withId(R.id.list)).check(isGone())
    }

    @Test
    fun trainsOfCategory_withAResult_showsCorrectResult() = runBlockingTest {
        // Launch the fragment in TRAINS_OF_CATEGORY mode
        val sampleCategoryName = sampleTrain2.categoryName
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_CATEGORY)
        args.putString(CATEGORY_NAME, sampleCategoryName)
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, args)

        onView(withId(R.id.empty_text)).check(isGone())
        onView(withId(R.id.empty_image)).check(isGone())
        onView(withId(R.id.list)).check(isVisible())
        onView(withText(sampleTrain2.trainName)).check(isVisible())
    }

    @Test
    fun launchedWithNoArgs_showsDefaults() = runBlockingTest {
        // Launch the fragment without args
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

        onView(withId(R.id.empty_text)).check(isGone())
        onView(withId(R.id.empty_image)).check(isGone())

        onView(withText(R.string.filter_by_category)).check(matches(isDisplayed()))
        onView(withText(R.string.filter_by_brand)).check(matches(isDisplayed()))
        onView(withHint(R.string.hint_search_keywords)).check(matches(isDisplayed()))
    }

    @Test
    fun launchedWithNoArgs_enterKeyword_showCorrectResults() = runBlockingTest {
        // Launch the fragment without args
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

        // Type a keyword
        onView(withId(R.id.search_keywords)).perform(typeText("red"), closeSoftKeyboard())
        onView(withId(R.id.search_btn)).perform(click())

        // Check correct results are shown
        onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))

        // Check non-corresponding items are not shown
        onView(withText(sampleTrain2.trainName)).check(doesNotExist())
    }

    @Test
    fun launchedWithNoArgs_enterKeyword_showsNoResults() = runBlockingTest {
        // Launch the fragment without args
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

        // Type a keyword
        onView(withId(R.id.search_keywords)).perform(typeText("NotTHere"), closeSoftKeyboard())
        onView(withId(R.id.search_btn)).perform(click())

        // Check correct results are shown
        onView(withText(R.string.no_results_for_search)).check(matches(isDisplayed()))
    }

    @Test
    fun chooseCategoryAndEnterKeyword_showsNoResults() = runBlockingTest {
        // Launch the fragment without args
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

        // Choose a category
        onView(withId(R.id.search_categorySpinner)).perform(click())
        onData(`is`(sampleTrain1.categoryName)).perform(click())

        // Type a keyword
        onView(withId(R.id.search_keywords)).perform(typeText("Blue"), closeSoftKeyboard())
        onView(withId(R.id.search_btn)).perform(click())

        // Check correct results are shown
        onView(withText(R.string.no_results_for_search)).check(matches(isDisplayed()))
    }

    @Test
    fun chooseBrandAndEnterKeyword_showsNoResults() = runBlockingTest {
        // Launch the fragment without args
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

        // Choose a brand
        onView(withId(R.id.search_brandSpinner)).perform(click())
        onData(`is`(sampleTrain1.brandName)).perform(click())

        // Type a keyword
        onView(withId(R.id.search_keywords)).perform(typeText("BM"), closeSoftKeyboard())
        onView(withId(R.id.search_btn)).perform(click())

        // Check correct results are shown
        onView(withText(R.string.no_results_for_search)).check(matches(isDisplayed()))
    }

    @Test
    fun chooseCategoryAndEnterKeyword_showsResults() = runBlockingTest {
        // Launch the fragment without args
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

        // Choose a category
        onView(withId(R.id.search_categorySpinner)).perform(click())
        onData(`is`(sampleTrain1.categoryName)).perform(click())

        // Type a keyword
        onView(withId(R.id.search_keywords)).perform(typeText("rEd"), closeSoftKeyboard())
        onView(withId(R.id.search_btn)).perform(click())

        // Check correct results are shown
        onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))
        onView(withText(sampleTrain2.trainName)).check(doesNotExist())
    }

    @Test
    fun chooseBrandAndEnterKeyword_showsResults() = runBlockingTest {
        // Launch the fragment without args
        HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

        // Choose a brand
        onView(withId(R.id.search_brandSpinner)).perform(click())
        onData(`is`(sampleTrain2.brandName)).perform(click())

        // Type a keyword
        onView(withId(R.id.search_keywords)).perform(typeText("blue"), closeSoftKeyboard())
        onView(withId(R.id.search_btn)).perform(click())

        // Check correct results are shown
        onView(withText(sampleTrain2.trainName)).check(matches(isDisplayed()))
        onView(withText(sampleTrain1.trainName)).check(doesNotExist())
    }

    @Test
    fun chooseCategoryBrandAndEnterKeyword_showsResults() =
        runBlockingTest {
            // Launch the fragment without args
            HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

            // Choose a category
            onView(withId(R.id.search_categorySpinner)).perform(click())
            onData(`is`(sampleTrain1.categoryName)).perform(click())

            // Choose a brand
            onView(withId(R.id.search_brandSpinner)).perform(click())
            onData(`is`(sampleTrain1.brandName)).perform(click())

            // Type a keyword
            onView(withId(R.id.search_keywords)).perform(typeText("red"), closeSoftKeyboard())
            onView(withId(R.id.search_btn)).perform(click())

            // Check correct results are shown
            onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))
            onView(withText(sampleTrain2.trainName)).check(doesNotExist())
        }

    @Test
    fun chooseCategoryBrandAndEnterKeyword_showsNoResults() =
        runBlockingTest {
            // Launch the fragment without args
            HiltFragmentScenario.launchInContainer(FilterTrainFragment::class.java, Bundle())

            // Choose a category
            onView(withId(R.id.search_categorySpinner)).perform(click())
            onData(`is`(sampleTrain1.categoryName)).perform(click())

            // Choose a brand
            onView(withId(R.id.search_brandSpinner)).perform(click())
            onData(`is`(sampleTrain2.brandName)).perform(click())

            // Type a keyword
            onView(withId(R.id.search_keywords)).perform(typeText("red"), closeSoftKeyboard())
            onView(withId(R.id.search_btn)).perform(click())

            // Check correct results are shown
            onView(withText(R.string.no_results_for_search)).check(matches(isDisplayed()))
        }
}
