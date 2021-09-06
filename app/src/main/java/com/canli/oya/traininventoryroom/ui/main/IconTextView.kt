package com.canli.oya.traininventoryroom.ui.main

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.canli.oya.traininventoryroom.R
import com.facebook.widget.text.span.BetterImageSpan


class IconTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null)
    : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

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
                val plusIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_plus_with_circle, null)
                plusIcon?.mutate()
                plusIcon?.setBounds(0, 0, 100, 100)
                spannable.setSpan(BetterImageSpan(plusIcon, BetterImageSpan.ALIGN_CENTER), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                super.setText(spannable, type)
            }
        }
    }
}