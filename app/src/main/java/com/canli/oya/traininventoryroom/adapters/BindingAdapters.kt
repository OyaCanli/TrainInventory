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
fun setVisible(view: View, visible: Boolean) {
    if (visible) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}
