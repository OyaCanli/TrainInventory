package com.canli.oya.traininventoryroom.adapters

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

