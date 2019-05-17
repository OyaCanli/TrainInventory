package com.canli.oya.traininventoryroom.adapters

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.canli.oya.traininventoryroom.utils.GlideApp


@BindingAdapter("imageUrl", "placeholder")
fun setImageUrl(view: ImageView, url: String?, placeHolder: Drawable) {
    GlideApp.with(view.context)
            .load(url)
            .centerCrop()
            .placeholder(placeHolder)
            .into(view)
}

@BindingAdapter("visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
