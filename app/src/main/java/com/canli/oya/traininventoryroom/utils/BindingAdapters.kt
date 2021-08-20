package com.canli.oya.traininventoryroom.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.canli.oya.traininventoryroom.R


@BindingAdapter("imageUrl", "placeholder")
fun ImageView.bindImage(url: String?, placeHolder: Drawable?) {
    url?.let {
        val imgUri = it
            .toUri()
            .buildUpon()
            .scheme("https")
            .build()
        this.load(imgUri) {
            fallback(placeHolder)
        }
    }
}

fun ImageView.bindImage(url: String?) {
    url?.let {
        it.toUri()
            .buildUpon()
            .scheme("https")
            .build()
    }
}

@BindingAdapter("visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("itemDivider")
fun RecyclerView.addItemDivider(hasItemDivider : Boolean) {
    val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
    divider.setDrawable(ShapeDrawable().apply {
        intrinsicHeight = context.resources.getDimensionPixelOffset(R.dimen.divider_height)
        paint.color = context.resources.getColor(R.color.divider_color)
    })
    addItemDecoration(divider)
}

@BindingAdapter("numerator")
fun TextView.numerate(position : Int){
    text = "${position + 1}."
}
