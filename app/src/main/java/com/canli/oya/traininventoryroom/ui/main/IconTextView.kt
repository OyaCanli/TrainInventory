package com.canli.oya.traininventoryroom.ui.main

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.widget.TextView
import com.canli.oya.traininventoryroom.R
import com.facebook.widget.text.span.BetterImageSpan


class IconTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null)
    : TextView(context, attrs) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        if(text.isNullOrBlank()){
            super.setText(text, type)
        } else {
            val startIndex = text.indexOf("plus")
            if(startIndex == -1){
                super.setText(text, type)
            } else {
                val endIndex = startIndex + 4
                val spannable = SpannableString(text)
                val plusIcon = context.resources.getDrawable(R.drawable.ic_plus_with_circle)
                plusIcon.mutate()
                plusIcon.setBounds(0, 0, 100, 100)
                spannable.setSpan(BetterImageSpan(plusIcon, BetterImageSpan.ALIGN_CENTER), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                super.setText(spannable, type)
            }
        }
    }
}