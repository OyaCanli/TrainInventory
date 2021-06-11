package com.canli.oya.traininventoryroom.endtoendtests

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.di.AppModule
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.hasSelectedItem
import com.canli.oya.traininventoryroom.utils.withIconResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(AppModule::class)
@HiltAndroidTest
class NavigationTests {

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

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun bottomNavigation_correctItemsAreSetSelected(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //Verify train item is selected by default and train list fragment is on screen
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.trainListFragment)))
        onView(withText(R.string.all_trains)).check(matches(withParent(withId(R.id.toolbar))))

        //Click on search menu item and verify that filter frag is shown and search menu item seems selected
        onView(withId(R.id.filterTrainFragment)).perform(click())
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.filterTrainFragment)))
        onView(withText(R.string.search_trains)).check(matches(withParent(withId(R.id.toolbar))))

        //Click on brands menu item and verify that brands frag is shown and brand menu item seems selected
        onView(withId(R.id.brandListFragment)).perform(click())
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.brandListFragment)))
        onView(withText(R.string.all_brands)).check(matches(withParent(withId(R.id.toolbar))))

        //Click on categories menu item and verify that trains frag is shown and train menu item seems selected
        onView(withId(R.id.categoryListFragment)).perform(click())
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.categoryListFragment)))
        onView(withText(R.string.all_categories)).check(matches(withParent(withId(R.id.toolbar))))

        //Press back and verify that we are in home screen and train menu item is selected
        Espresso.pressBack()
        onView(withId(R.id.navigation)).check(matches(hasSelectedItem(R.id.trainListFragment)))
        onView(withText(R.string.all_trains)).check(matches(withParent(withId(R.id.toolbar))))

        activityScenario.close()
    }
    
    @Test
    fun clickedPlusIcon_BecomesCancelIcon() = runBlocking {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        /*Verify that + icon is shown on the action menu at launch. Then click on plus button.
        Verify that + icon is replaced with x icon*/
        onView(withId(R.id.categoryListFragment)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_plus_to_cross)))
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))

        //Then switch to brands frag and verify that + icon is shown on the action menu
        onView(withId(R.id.brandListFragment)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_plus_to_cross)))

        //Click on plus item and verify it becomes x. Then click back and verify that it return back to plus
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_cross_to_plus)))
        onView(withId(R.id.action_add)).perform(click())
        onView(withId(R.id.action_add)).check(matches(withIconResource(R.drawable.avd_plus_to_cross)))

        activityScenario.close()
    }
}