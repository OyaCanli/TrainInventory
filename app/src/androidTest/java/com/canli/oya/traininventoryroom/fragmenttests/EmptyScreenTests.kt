package com.canli.oya.traininventoryroom.fragmenttests

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.HiltFragmentScenario
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.datasource.FakeBrandDataSource
import com.canli.oya.traininventoryroom.datasource.FakeCategoryDataSource
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.di.DataSourceModule
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import com.canli.oya.traininventoryroom.utils.ALL_TRAIN
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.utils.isGone
import com.canli.oya.traininventoryroom.utils.isVisible
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
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

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
@RunWith(AndroidJUnit4::class)
class EmptyScreenTests {

    @Module
    @InstallIn(ApplicationComponent::class)
    object FakeDataModule {

        @Provides
        fun provideTrainDataSource() : ITrainDataSource = FakeTrainDataSource(mutableListOf())

        @Provides
        fun provideCategoryDataSource() : IBrandCategoryDataSource<Category> = FakeCategoryDataSource(mutableListOf())

        @Provides
        fun provideBrandDataSource() : IBrandCategoryDataSource<Brand> = FakeBrandDataSource(mutableListOf())

        @Provides
        fun provideDatabase() : TrainDatabase = Mockito.mock(TrainDatabase::class.java)

        @IODispatcher
        @Provides
        fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
    }

    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var rule = RuleChain.outerRule(hiltRule).
    around(InstantTaskExecutorRule())

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    //Launch with an empty list. Verify that empty text and image are shown and verify that the list is not shown
    @Test
    fun brandList_withEmptyList_emptyScreenIsShown() {
        runBlockingTest {
            HiltFragmentScenario.launchInContainer(BrandListFragment::class.java, Bundle())

            onView(ViewMatchers.withId(R.id.empty_text)).check(isVisible())
            onView(ViewMatchers.withId(R.id.empty_image)).check(isVisible())
            onView(ViewMatchers.withId(R.id.list)).check(isGone())
        }
    }

    //Launch with an empty list. Verify that empty text and image are shown and verify that the list is not shown
    @Test
    fun categoryList_withEmptyList_emptyScreenIsShown() {
        runBlockingTest {
            HiltFragmentScenario.launchInContainer(CategoryListFragment::class.java, Bundle())

            onView(ViewMatchers.withId(R.id.empty_text)).check(isVisible())
            onView(ViewMatchers.withId(R.id.empty_image)).check(isVisible())
            onView(ViewMatchers.withId(R.id.list)).check(isGone())
        }
    }

    //Launch with an empty list. Verify that empty text and image are shown and verify that the list is not shown
    @Test
    fun allTrains_withEmptyList_emptyScreenIsShown() {
        runBlockingTest {
            //Launch the fragment in All Trains mode
            val args = Bundle()
            args.putString(INTENT_REQUEST_CODE, ALL_TRAIN)
            HiltFragmentScenario.launchInContainer(TrainListFragment::class.java, Bundle())

            //Check if empty layout is displayed
            onView(ViewMatchers.withId(R.id.empty_text)).check(isVisible())
            onView(ViewMatchers.withId(R.id.empty_image)).check(isVisible())
        }
    }
}
