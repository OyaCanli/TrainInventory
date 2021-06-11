package com.canli.oya.traininventoryroom.endtoendtests

import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
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
import com.canli.oya.traininventoryroom.utils.withIconResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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
class CategoryTests {

    @Module
    @InstallIn(ApplicationComponent::class)
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

    @Inject
    lateinit var database: TrainDatabase

    @Before
    fun init() {
        hiltRule.inject()
    }

    val sampleCategoryName = "New category"
    val updateCategoryName = "Updated name"


    @After
    fun closeDb() = database.close()


    @Test
    fun clickAddCategory_addACategory_isAddedToTheCategoryList() = runBlockingTest {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Click + menu item and verify child frag becomes visible with empty fields
        onView(withId(R.id.categoryListFragment)).perform(click())
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.addCategory_editCatName)).check(matches(isDisplayed()))
        onView(withId(R.id.addCategory_editCatName)).check(matches(withText("")))
        onView(withId(R.id.addCategory_saveBtn)).check(matches(isDisplayed()))
        //Verify + menu icon turned to a cancel icon
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))

        /*Add a category, click save and verify that edittext is cleared
        and new category is added to the list*/
        onView(withId(R.id.addCategory_editCatName)).perform(typeText(sampleCategoryName))
        onView(withId(R.id.addCategory_saveBtn)).perform(click())
        //Verify edittext is cleared after category is saved
        onView(withId(R.id.addCategory_editCatName)).check(matches(withText("")))
        onView(withText(sampleCategoryName)).check(matches(isDisplayed()))

        //Click cancel button and verify add category frag is removed
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.addCategory_editCatName)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun editAndUpdateCategory_categoryIsUpdatedOnTheList() = runBlocking {
        //Insert a sample category to the database
        val sampleCategory = CategoryEntity(categoryName = sampleCategoryName)
        database.categoryDao().insert(sampleCategory)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Click edit on the item and verify AddCategory becomes visible with correct fields
        onView(withId(R.id.categoryListFragment)).perform(click())
        onView(withId(R.id.list))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.category_item_edit_icon)))
        onView(withId(R.id.addCategory_editCatName)).check(matches(isDisplayed()))
        onView(withId(R.id.addCategory_editCatName)).check(matches(withText(sampleCategoryName)))
        //Verify + menu icon turned to a cancel icon
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))

        //Edit the item
        onView(withId(R.id.addCategory_editCatName)).perform(replaceText(updateCategoryName))
        onView(withId(R.id.addCategory_saveBtn)).perform(click())
        onView(withId(R.id.action_add)).perform(click())
        //Verify the category name on the list is updated
        onView(withText(sampleCategoryName)).check(doesNotExist())
        onView(withText(updateCategoryName)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    /*Launch the activity. Click on a train icon on the category and
    verify that trains frag is launched and correct results are shown*/
    @Test
    fun clickTrainIconOnACategory_trainsFromThatCategoryShown() = runBlocking {
        //Insert sample data to the database
        database.categoryDao().insert(sampleCategory1.toCategoryEntity())
        database.brandDao().insert(sampleBrand1.toBrandEntity())
        database.trainDao().insert(sampleTrain1.toTrainEntity())

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Click on train icon on the sample category
        onView(withId(R.id.categoryListFragment)).perform(click())
        onView(withId(R.id.list))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.category_item_train_icon)))

        onView(withText(R.string.search_trains)).check(matches(withParent(withId(R.id.toolbar))))
        onView(withText(sampleTrain1.categoryName)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_image)).check(isGone())
        onView(withId(R.id.empty_text)).check(isGone())

        activityScenario.close()
    }
}