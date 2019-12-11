package com.canli.oya.traininventoryroom.utils

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.canli.oya.traininventoryroom.R


fun getItemDivider(context: Context): DividerItemDecoration {
    val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
    divider.setDrawable(ShapeDrawable().apply {
        intrinsicHeight = context.resources.getDimensionPixelOffset(R.dimen.divider_height)
        paint.color = context.resources.getColor(R.color.divider_color)
    })
    return divider
}


