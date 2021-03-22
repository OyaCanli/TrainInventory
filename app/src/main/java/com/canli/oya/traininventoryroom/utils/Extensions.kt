package com.canli.oya.traininventoryroom.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes

private var toast : Toast? = null

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
    //This is for closing soft keyboard if user navigates to another fragment while keyboard was open
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusedView = currentFocus
    focusedView?.run {
        clearFocus()
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}