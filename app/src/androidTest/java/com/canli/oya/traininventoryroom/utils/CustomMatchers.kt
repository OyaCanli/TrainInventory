package com.canli.oya.traininventoryroom.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.MatcherAssert.assertThat


fun isVisible(): ViewAssertion? {
    return ViewAssertion { view, noView -> assertThat(view, VisibilityMatcher(View.VISIBLE)) }
}

fun isGone(): ViewAssertion? {
    return ViewAssertion { view, noView -> assertThat(view, VisibilityMatcher(View.GONE)) }
}

fun isInvisible(): ViewAssertion? {
    return ViewAssertion { view, noView -> assertThat(view, VisibilityMatcher(View.INVISIBLE)) }
}

private class VisibilityMatcher(private val visibility: Int) : BaseMatcher<View?>() {
    override fun describeTo(description: Description) {
        val visibilityName: String
        visibilityName = if (visibility == View.GONE) "GONE" else if (visibility == View.VISIBLE) "VISIBLE" else "INVISIBLE"
        description.appendText("View visibility must has equals $visibilityName")
    }

    override fun matches(o: Any): Boolean {
        if (o == null) {
            if (visibility == View.GONE || visibility == View.INVISIBLE) return true else if (visibility == View.VISIBLE) return false
        }
        require(o is View) { "Object must be instance of View. Object is instance of $o" }
        return (o as View).getVisibility() === visibility
    }
}

fun clickOnChildWithId(viewId: Int) = object : ViewAction {
    override fun getConstraints() = null

    override fun getDescription() = "Click on a child view with specified id."

    override fun perform(uiController: UiController, view: View) = click().perform(uiController, view.findViewById<View>(viewId))
}