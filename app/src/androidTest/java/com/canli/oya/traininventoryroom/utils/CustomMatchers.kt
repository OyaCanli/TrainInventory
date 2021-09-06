package com.canli.oya.traininventoryroom.utils

import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.test.espresso.Root
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.BoundedMatcher
import com.canli.oya.traininventoryroom.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.TypeSafeMatcher
import timber.log.Timber

fun isVisible(): ViewAssertion {
    return ViewAssertion { view, noView -> assertThat(view, VisibilityMatcher(View.VISIBLE)) }
}

fun isGone(): ViewAssertion {
    return ViewAssertion { view, _ -> assertThat(view, VisibilityMatcher(View.GONE)) }
}

fun isInvisible(): ViewAssertion {
    return ViewAssertion { view, _ -> assertThat(view, VisibilityMatcher(View.INVISIBLE)) }
}

private class VisibilityMatcher(private val visibility: Int) : BaseMatcher<View?>() {
    override fun describeTo(description: Description) {
        val visibilityName: String = if (visibility == View.GONE) "GONE" else if (visibility == View.VISIBLE) "VISIBLE" else "INVISIBLE"
        description.appendText("View visibility must be $visibilityName")
    }

    override fun matches(o: Any): Boolean {
        require(o is View) { "Object must be instance of View. Object is instance of $o" }
        return o.visibility == visibility
    }
}

class ToastMatcher : TypeSafeMatcher<Root?>() {

    override fun describeTo(description: Description?) {
        description?.appendText("is toast")
    }

    override fun matchesSafely(item: Root?): Boolean {
        val type: Int? = item?.windowLayoutParams?.get()?.type
        if (type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {
            val windowToken: IBinder = item.decorView.windowToken
            val appToken: IBinder = item.decorView.applicationWindowToken
            if (windowToken === appToken) { // means this window isn't contained by any other windows.
                return true
            }
        }
        return false
    }
}

fun clickOnChildWithId(viewId: Int) = object : ViewAction {
    override fun getConstraints() = null

    override fun getDescription() = "Click on a child view with specified id."

    override fun perform(uiController: UiController, view: View) = click().perform(uiController, view.findViewById(viewId))
}

fun hasSelectedItem(id: Int): Matcher<View?> {
    return object : BoundedMatcher<View?, BottomNavigationView>(BottomNavigationView::class.java) {
        var checkedIds: MutableSet<Int> = HashSet()
        var itemFound = false
        var triedMatching = false
        override fun describeTo(description: Description) {
            if (!triedMatching) {
                description.appendText("BottomNavigationView")
                return
            }
            description.appendText("BottomNavigationView to have a checked item with id=")
            description.appendValue(id)
            if (itemFound) {
                description.appendText(", but selection was=")
                description.appendValue(checkedIds)
            } else {
                description.appendText(", but it doesn't have an item with such id")
            }
        }

        override fun matchesSafely(navigationView: BottomNavigationView): Boolean {
            triedMatching = true
            val menu: Menu = navigationView.menu
            for (i in 0 until menu.size()) {
                val item: MenuItem = menu.getItem(i)
                if (item.isChecked) {
                    checkedIds.add(item.itemId)
                }
                if (item.itemId == id) {
                    itemFound = true
                }
            }
            return checkedIds.contains(id)
        }
    }
}

fun withIconResource(@DrawableRes resourceId: Int): Matcher<View?> {
    return object : BoundedMatcher<View?, ActionMenuItemView>(ActionMenuItemView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("should have image drawable ${getTitleForResource(resourceId)}")
        }

        override fun matchesSafely(actionMenuItemView: ActionMenuItemView): Boolean {
            Timber.d("it has the drawable  ${actionMenuItemView.itemData.title}")
            return actionMenuItemView.itemData.title == getTitleForResource(resourceId)
        }
    }
}

fun getTitleForResource(resourceId: Int) = when (resourceId) {
    R.drawable.avd_plus_to_cross -> TITLE_PLUS
    R.drawable.avd_cross_to_plus -> TITLE_CROSS
    else -> throw IllegalArgumentException("No title specified for this resource id")
}
