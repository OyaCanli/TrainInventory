package com.canli.oya.traininventoryroom.ui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView


class IconTextView : TextView {

    constructor(context: Context) : super(context) {
        createView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        createView(context)
    }

    private fun createView(context: Context) {
        typeface = Typeface.createFromAsset(context.assets, "fa-solid-900.ttf")
    }
}