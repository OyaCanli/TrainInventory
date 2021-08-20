package com.canli.oya.traininventoryroom.endtoendtests

import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.entities.toBrandEntity
import com.canli.oya.traininventoryroom.data.entities.toCategoryEntity
import com.canli.oya.traininventoryroom.data.entities.toTrainEntity
import com.canli.oya.traininventoryroom.datasource.sampleBrand1
import com.canli.oya.traininventoryroom.datasource.sampleCategory1
import com.canli.oya.traininventoryroom.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.di.AppModule
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.clickOnChildWithId
import com.canli.oya.traininventoryroom.utils.isGone
import com.canli.oya.traininventoryroom.utils.isVisible
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.models.Train
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(AppModule::class)
@HiltAndroidTest
class TrainTests {

    @Module
    @InstallIn(SingletonComponent::class)
    class InMemoryDataModule {

        @Singleton
        @Provides
        fun provideDatabase() : TrainDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TrainDatabase::class.java
        ).build()
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Inject
    lateinit var database: TrainDatabase

    @After
    fun closeDb() = database.close()

    @Test
    fun clickAddTrain_isLaunchedWithEmptyAndDefaultValues() = runBlocking {
        database.categoryDao().insert(sampleCategory1.toCategoryEntity())
        database.brandDao().insert(sampleBrand1.toBrandEntity())

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Click trains from the bottom menu
        onView(withId(R.id.trainListFragment)).perform(click())

        //Click add button from action menu
        onView(withId(R.id.action_add)).perform(click())

        //Verify AddTrainFragment is launched with empty or default fields
        onView(withText(R.string.add_train)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withText(R.string.select_category)).check(matches(isDisplayed()))
        onView(withText(R.string.select_brand)).check(matches(isDisplayed()))
        onView(withId(R.id.editReference)).check(matches(withText("")))
        onView(withId(R.id.editTrainName)).check(matches(withText("")))
        onView(withId(R.id.editScale)).check(matches(withText("")))
        onView(withId(R.id.editLocation)).check(matches(withText("")))
        onView(withId(R.id.editTrainDescription)).check(matches(withText("")))
        //Verify bottom navigation is gone
        onView(withId(R.id.navigation)).check(isGone())

        //Fill in the widgets
        onView(withId(R.id.categorySpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(sampleCategory1.categoryName))).perform(click())
        onView(withId(R.id.brandSpinner)).perform(click())
        onView(withText(sampleBrand1.brandName)).perform(click())

        onView(withId(R.id.editReference)).perform(typeText(sampleTrain1.modelReference), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.editTrainName)).perform(scrollTo(), typeText(sampleTrain1.trainName), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.editScale)).perform(scrollTo(), click(), typeText(sampleTrain1.scale), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.editLocation)).perform(scrollTo(), typeText(sampleTrain1.location), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.editTrainDescription)).perform(scrollTo(), typeText(sampleTrain1.description), ViewActions.closeSoftKeyboard())

        //Click save button from action menu
        onView(withId(R.id.action_save)).perform(click())

        onView(withText(R.string.all_trains)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.navigation)).check(isVisible())
        onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun showTrainDetails_deleteItem() = runBlocking {
        addSampleData(sampleCategory1, sampleBrand1, sampleTrain1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Click trains from the bottom menu
        onView(withId(R.id.trainListFragment)).perform(click())

        //Click on the train item
        onView(withId(R.id.list))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        //Verify details frag is launched and show correct data
        onView(withText(sampleTrain1.trainName)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.details_brand)).check(matches(withText(sampleTrain1.brandName)))
        onView(withId(R.id.details_category)).check(matches(withText(sampleTrain1.categoryName)))
        onView(withId(R.id.details_reference)).check(matches(withText(sampleTrain1.modelReference)))
        onView(withId(R.id.details_scale)).check(matches(withText(sampleTrain1.scale)))
        onView(withId(R.id.details_description)).check(matches(withText(sampleTrain1.description)))
        onView(withId(R.id.details_location)).check(matches(withText(sampleTrain1.location)))

        //Click delete
        onView(withId(R.id.action_delete)).perform(click())
        //Verify a warning dialog is shown
        onView(withText(R.string.do_you_want_to_delete)).check(matches(isDisplayed()))
        //Confirm delete
        onView(withText(R.string.yes_delete)).perform(click())
        //Verify we are back at TrainListFragment and the train is deleted
        onView(withText(R.string.all_trains)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.empty_image)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editTrain_clickBack_warnOnlyIfModified(): Unit = runBlocking {
        addSampleData(sampleCategory1, sampleBrand1, sampleTrain1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Click trains from the bottom menu
        onView(withId(R.id.trainListFragment)).perform(click())

        //Click on the train item
        onView(withId(R.id.list)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        //Click edit button
        onView(withId(R.id.action_edit)).perform(click())
        //Verify we are on AddTrainFragment, in edit mode, with correct data set on edittexts
        onView(withText(R.string.edit_train)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.editTrainName)).check(matches(withText(sampleTrain1.trainName)))
        onView(withText(sampleTrain1.categoryName)).check(matches(isDisplayed()))
        onView(withText(sampleTrain1.brandName)).check(matches(isDisplayed()))

        Espresso.pressBack()
        //Verify we are back at details screen without a warning
        onView(withText(sampleTrain1.trainName)).check(matches(withParent(withId(R.id.toolbar))))
        //Click edit button again
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.editTrainName)).perform(replaceText("modified train name"))

        Espresso.pressBack()
        onView(withText(R.string.unsaved_changes_warning)).check(matches(isDisplayed()))

        activityScenario.close()

    }

    @Test
    fun editTrain_clickUp_warnOnlyIfModified(): Unit = runBlocking {
        addSampleData(sampleCategory1, sampleBrand1, sampleTrain1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Click trains from the bottom menu
        onView(withId(R.id.trainListFragment)).perform(click())

        //Click on the train item
        onView(withId(R.id.list)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        //Click edit button
        onView(withId(R.id.action_edit)).perform(click())
        //Verify we are on AddTrainFragment, in edit mode, with correct data set on edittexts
        onView(withText(R.string.edit_train)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withId(R.id.editTrainName)).check(matches(withText(sampleTrain1.trainName)))
        onView(withText(sampleTrain1.categoryName)).check(matches(isDisplayed()))
        onView(withText(sampleTrain1.brandName)).check(matches(isDisplayed()))

        //Click up
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description)).perform(click())
        //Verify we are back at details screen without a warning
        onView(withText(sampleTrain1.trainName)).check(matches(withParent(withId(R.id.toolbar))))
        //Click edit button again
        onView(withId(R.id.action_edit)).perform(click())
        onView(withId(R.id.editTrainName)).perform(replaceText("modified train name"))

        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description)).perform(click())
        onView(withText(R.string.unsaved_changes_warning)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteATrain_findItOnTrashAndRestoreIt()= runBlocking {
        addSampleData(sampleCategory1, sampleBrand1, sampleTrain1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Swipe and delete item
        onView(withId(R.id.list))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft()))
        onView(withId(R.id.list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.confirm_delete_btn)))

        //Navigate to trash
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.trash)).perform(click())

        //Verify that deleted item is seen in the trash
        onView(withText(R.string.trash)).check(matches(isDisplayed()))
        onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))

        //Click on restore
        onView(withId(R.id.list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.trash_item_restore)))

        //Verify it disappears from trash
        onView(withId(R.id.empty_image)).check(matches(isDisplayed()))

        //Navigate up
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description)).perform(click())

        //Check that the train is added back in train list
        onView(withText(R.string.all_trains)).check(matches(isDisplayed()))
        onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    private suspend fun addSampleData(
        sampleCategory1: Category,
        sampleBrand1: Brand,
        sampleTrain1: Train
    ) {
        database.categoryDao().insert(sampleCategory1.toCategoryEntity())
        database.brandDao().insert(sampleBrand1.toBrandEntity())
        database.trainDao().insert(sampleTrain1.toTrainEntity())
    }
}