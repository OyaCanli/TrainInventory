package com.canli.oya.traininventoryroom.adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.utils.GlideApp


@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url: String?) {
    GlideApp.with(view.context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.ic_gallery)
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
