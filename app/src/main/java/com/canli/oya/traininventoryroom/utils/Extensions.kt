package com.canli.oya.traininventoryroom.utils

import android.content.Context
import android.view.Gravity
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