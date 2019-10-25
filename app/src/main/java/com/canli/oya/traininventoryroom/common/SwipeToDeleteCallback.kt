package com.canli.oya.traininventoryroom.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import kotlin.math.roundToInt


abstract class SwipeToDeleteCallback(val context : Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    private var background: ColorDrawable = ColorDrawable(context.resources.getColor(R.color.colorAccent))
    private val deleteIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete)!!
    private val deleteIconMargin = context.resources.getDimension(R.dimen.half_margin)
    internal var initiated: Boolean = false

    private fun init() {
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        initiated = true
    }

    // not important, we don't want drag & drop
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.adapterPosition == -1) {
            // not interested in those
            return
        }

        if (!initiated) {
            init()
        }

        // draw red background
        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(c)

        // draw x mark
        val itemHeight = itemView.bottom - itemView.top
        val intrinsicWidth = deleteIcon.intrinsicWidth
        val intrinsicHeight = deleteIcon.intrinsicWidth

        val deleteIconLeft = (itemView.right - deleteIconMargin - intrinsicWidth).roundToInt()
        val deleteIconRight = (itemView.right - deleteIconMargin).roundToInt()
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconBottom = deleteIconTop + intrinsicHeight
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)

        deleteIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}