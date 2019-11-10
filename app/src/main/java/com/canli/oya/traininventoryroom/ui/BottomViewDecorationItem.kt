package com.canli.oya.traininventoryroom.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.canli.oya.traininventoryroom.R

class BottomViewDecorationItem @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var colorSelected = resources.getColor(R.color.colorAccent)
    var colorInActive = resources.getColor(R.color.colorPrimary)

    var itemWidth : Float = 0f
    var itemHeight : Float = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val path = Path()

    var checked : Boolean = false
        set(value) {
            field = value
            postInvalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = if(checked) colorSelected else colorInActive

        itemWidth = width*1f
        itemHeight = height*1f

        path.reset()
        path.moveTo(0f, itemHeight)
        path.quadTo(0f, 0f, itemHeight, 0f)
        path.lineTo(itemWidth-itemHeight, 0f)
        path.quadTo(itemWidth, 0f, itemWidth, itemHeight)
        path.lineTo(0f, itemHeight)

        canvas.drawPath(path, paint)
    }

}