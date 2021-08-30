package com.canli.oya.traininventoryroom.fragmenttests

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.datasource.FakeBrandDataSource
import com.canli.oya.traininventoryroom.datasource.FakeCategoryDataSource
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.di.DataSourceModule
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.ui.trash.TrashListFragment
import com.canli.oya.traininventoryroom.utils.clickOnChildWithId
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.time.LocalDate
import javax.inject.Inject

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
@RunWith(AndroidJUnit4::class)
class TrashFragmentTests {

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
        fun provideDatabase() : TrainDatabase = Mockito.mock(TrainDatabase::class.java)

        @IODispatcher
        @Provides
        fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
    }

    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var rule = RuleChain.outerRule(hiltRule).
    around(InstantTaskExecutorRule())

    @Inject
    lateinit var dataSource : ITrainDataSource

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun whenNoTrash_showsEmptyMessage() {
        runBlockingTest {
            //Launch trash fragment with no trash
            HiltFragmentScenario.launchInContainer(TrashListFragment::class.java, Bundle())

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
            HiltFragmentScenario.launchInContainer(TrashListFragment::class.java, Bundle())

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
            HiltFragmentScenario.launchInContainer(TrashListFragment::class.java, Bundle())

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
            HiltFragmentScenario.launchInContainer(TrashListFragment::class.java, Bundle())

            //Click on delete on item
            onView(withId(R.id.list)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.trash_item_delete)))
            onView(withText(R.string.do_you_want_to_permanently_delete_train)).check(matches(isDisplayed()))
        }
    }
}
