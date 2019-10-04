package com.canli.oya.traininventoryroom.common

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter


@BindingAdapter("imageUrl", "placeholder")
fun ImageView.setImageWithGlide(url: String?, placeHolder: Drawable) {
    GlideApp.with(context)
            .load(url)
            .centerCrop()
            .placeholder(placeHolder)
            .into(this)
}

@BindingAdapter("visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
