package com.canli.oya.traininventoryroom.fragmenttests


import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canlioya.testresources.datasource.FakeBrandDataSource
import com.canlioya.testresources.datasource.FakeCategoryDataSource
import com.canlioya.testresources.datasource.FakeTrainDataSource
import com.canlioya.testresources.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.di.DataSourceModule
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragment
import com.canli.oya.traininventoryroom.utils.TRAIN_ID
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
class TrainDetailsFragmentTest {

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

    private lateinit var scenario: HiltFragmentScenario<TrainDetailsFragment, HiltFragmentScenario.EmptyFragmentActivity>


    @Before
    fun setUp() {
        hiltRule.inject()

        val args = Bundle()
        args.putInt(TRAIN_ID, sampleTrain1.trainId)
        scenario = HiltFragmentScenario.launchInContainer(TrainDetailsFragment::class.java, args)
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
        onView(withId(R.id.details_location)).check(matches(withText(sampleTrain1.location)))
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
