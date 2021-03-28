package com.canli.oya.traininventoryroom.endtoendtests

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.datasource.sampleBrand1
import com.canli.oya.traininventoryroom.datasource.sampleCategory1
import com.canli.oya.traininventoryroom.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.inmemory.DaggerInMemoryTestComponent
import com.canli.oya.traininventoryroom.di.inmemory.InMemoryTestComponent
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.DataBindingIdlingResource
import com.canli.oya.traininventoryroom.utils.clickOnChildWithId
import com.canli.oya.traininventoryroom.utils.monitorActivity
import com.canli.oya.traininventoryroom.utils.withIconResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
class BrandTests {

    // An Idling Resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Inject
    lateinit var database: TrainDatabase

    val sampleBrandName = "New brand"
    val sampleWebAddress = "https://www.google.com/"

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<TrainApplication>()
        ComponentProvider.getInstance(app).daggerComponent = DaggerInMemoryTestComponent.builder()
                .testAppModule(TestAppModule(app))
                .build()
        (ComponentProvider.getInstance(app).daggerComponent as InMemoryTestComponent).inject(this)
    }

    @After
    fun closeDb() = database.close()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun clickAddBrand_addABrand_isAddedToBrandList() = runBlocking {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click brands from the bottom menu
        onView(withId(R.id.brandListFragment)).perform(click())

        //Click + menu item and verify child frag becomes visible with empty fields
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.addBrand_editBrandName)).check(matches(isDisplayed()))
        onView(withId(R.id.addBrand_editBrandName)).check(matches(withText("")))
        onView(withId(R.id.addBrand_editWeb)).check(matches(withText("")))
        onView(withId(R.id.addBrand_saveBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.addBrand_image)).check(matches(isDisplayed()))
        //Verify + menu icon turned to a cancel icon
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))

/*      Add a brand, click save and verify that edittext is cleared
        and new brand is added to the list*/
        onView(withId(R.id.addBrand_editBrandName)).perform(typeText(sampleBrandName), closeSoftKeyboard())
        onView(withId(R.id.addBrand_editWeb)).perform(typeText(sampleBrandName), closeSoftKeyboard())
        onView(withId(R.id.addBrand_saveBtn)).perform(scrollTo(), click())
        //Verify edittext is cleared after brand is saved
        onView(withId(R.id.addBrand_editBrandName)).check(matches(withText("")))

        onView(withId(R.id.action_add)).perform(click())
        onView(withText(sampleBrandName)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editAndUpdateBrand_brandIsUpdatedOnTheList() = runBlocking {
        //Insert a sample category to the database
        val sampleBrand = BrandEntry(brandName = sampleBrandName, webUrl = sampleWebAddress)
        database.brandDao().insert(sampleBrand)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click brands from the bottom menu
        onView(withId(R.id.brandListFragment)).perform(click())

        //Click edit on the item and verify AddBrand becomes visible with correct fields
        onView(withId(R.id.list))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.brand_item_edit_icon)))
        onView(withId(R.id.addBrand_editBrandName)).check(matches(isDisplayed()))
        onView(withId(R.id.addBrand_editBrandName)).check(matches(withText(sampleBrandName)))
        onView(withId(R.id.addBrand_editWeb)).check(matches(withText(sampleWebAddress)))
        //Verify + menu icon turned to a cancel icon
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))

        val updatedBrandName = "Updated brand"
        //Edit the item
        onView(withId(R.id.addBrand_editBrandName)).perform(click(), replaceText(updatedBrandName), closeSoftKeyboard())
        onView(withId(R.id.addBrand_saveBtn)).perform(scrollTo(), click())
        //Close the child fragment
        onView(withId(R.id.action_add)).perform(click())
        //Verify that the brand name on the list is updated
        onView(withText(sampleBrandName)).check(doesNotExist())
        onView(withText(updatedBrandName)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    /*Launch the activity. click brands from the bottom menu. Click on a train icon on a brand
     and verify that trains frag is launched and correct results are shown*/
    @Test
    fun clickTrainIconOnABrand_trainsFromThatBrandShown() = runBlocking {
        //Insert sample data to the database
        database.categoryDao().insert(sampleCategory1)
        database.brandDao().insert(sampleBrand1)
        database.trainDao().insert(sampleTrain1)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click brands from the bottom menu
        onView(withId(R.id.brandListFragment)).perform(click())

        //Click on train icon on the sample category
        onView(withId(R.id.list))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnChildWithId(R.id.brand_item_train_icon)))

        onView(withText("Trains of the brand ${sampleBrand1.brandName}")).check(matches(withParent(withId(R.id.toolbar))))
        onView(withText(sampleTrain1.trainName)).check(matches(isDisplayed()))

        activityScenario.close()
    }
}
