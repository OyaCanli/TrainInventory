package com.canli.oya.traininventoryroom.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.ui.base.setMenuIcon
import timber.log.Timber

private var toast: Toast? = null

fun Context.shortToast(message: String) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.shortToast(@StringRes message: Int) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.longToast(message: String) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.longToast(@StringRes message: Int) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Activity.clearFocusAndHideKeyboard() {
    // This is for closing soft keyboard if user navigates to another fragment while keyboard was open
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusedView = currentFocus
    focusedView?.run {
        clearFocus()
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}

fun MenuItem.blinkAddMenuItem(@DrawableRes iconToSet: Int) {
    Timber.d("blinkAddMenuItem is called")
    if (Build.VERSION.SDK_INT >= 23) {
        setIcon(R.drawable.avd_blinking_plus)
        val blinkingAnim = this.icon as? AnimatedVectorDrawable
        blinkingAnim?.clearAnimationCallbacks()
        blinkingAnim?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationStart(drawable: Drawable) {
                Timber.d("blink animation started")
            }

            override fun onAnimationEnd(drawable: Drawable) {
                setMenuIcon(iconToSet)
            }
        })
        blinkingAnim?.start()
    }
}
