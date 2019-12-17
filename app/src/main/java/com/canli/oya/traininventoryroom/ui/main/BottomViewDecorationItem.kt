package com.canli.oya.traininventoryroom.ui.main

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
    val frameColor = resources.getColor(R.color.colorPrimary)

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

        itemHeight = height*1f

        val curveCount = width / (height*2)

        path.reset()
        path.moveTo(0f, itemHeight)

        for(curveNo in 0 until curveCount){
            path.quadTo(curveNo*2*itemHeight, 0f, ((curveNo*2)+1)*itemHeight, 0f)
            path.quadTo(((curveNo*2)+2)*itemHeight, 0f, ((curveNo*2)+2)*itemHeight, itemHeight)
        }

        path.lineTo(0f, itemHeight)

        paint.color = if(checked) colorSelected else colorInActive
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)

        paint.color = frameColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawPath(path, paint)
    }

}