package com.canli.oya.traininventoryroom.fragmenttests

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canlioya.testresources.datasource.FakeBrandDataSource
import com.canlioya.testresources.datasource.FakeCategoryDataSource
import com.canlioya.testresources.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.di.DataSourceModule
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import com.canli.oya.traininventoryroom.utils.isGone
import com.canli.oya.traininventoryroom.utils.isVisible
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
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
class TrainListFragmentTest{

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeDataModule {

        @Provides
        fun provideTrainDataSource() : ITrainDataSource = FakeTrainDataSource()

        @Provides
        fun provideCategoryDataSource() : IBrandCategoryDataSource<Category> = FakeCategoryDataSource()

        @Provides
        fun provideBrandDataSource() : IBrandCategoryDataSource<Brand> = FakeBrandDataSource()

        @Provides
        fun provideDatabase() : com.canlioya.local.TrainDatabase = Mockito.mock(com.canlioya.local.TrainDatabase::class.java)

        @IODispatcher
        @Provides
        fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
    }

    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var rule = RuleChain.outerRule(hiltRule).
    around(InstantTaskExecutorRule())


    //Launch with a sample list. Verify that empty layout is not shown and the list is shown with correct items
    @Test
    fun allTrains_withSampleList_emptyScreenIsNotShown() {
        runBlockingTest {
            //Launch the fragment in ALL_TRAINS mode
            HiltFragmentScenario.launchInContainer(TrainListFragment::class.java, Bundle())

            //Verify that empty layout is not displayed and that the list is displayed
            onView(withId(R.id.empty_text)).check(isGone())
            onView(withId(R.id.empty_image)).check(isGone())
            onView(withId(R.id.list)).check(isVisible())
        }
    }

    @Test
    fun swipingItem_revealsDeleteConfirmation() {
        runBlockingTest {
            //Launch the fragment in ALL_TRAINS mode
            HiltFragmentScenario.launchInContainer(TrainListFragment::class.java, Bundle())

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
