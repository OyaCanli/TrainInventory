package com.canli.oya.traininventoryroom.utils

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG

fun Context.toast(text: CharSequence) {
    Toast.makeText(this, text, LENGTH_LONG).show()
}

fun Context.toast(textResource: Int) {
    Toast.makeText(this, textResource, LENGTH_LONG).show()
}
