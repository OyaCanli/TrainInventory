package com.canli.oya.traininventoryroom.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.view.children
import com.canli.oya.traininventoryroom.R
import timber.log.Timber


class BottomViewDecoration : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init(attrs)
    }

    private val defaultColorSelected = resources.getColor(R.color.colorAccent)
    private val defaultColorInActive = resources.getColor(R.color.colorPrimary)

    private fun init(set: AttributeSet?) {
        if (set == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(set, R.styleable.BottomViewDecoration)
        val colorSelected = typedArray.getColor(R.styleable.BottomViewDecoration_colorSelected, defaultColorSelected)
        val colorInactive = typedArray.getColor(R.styleable.BottomViewDecoration_colorInactive, defaultColorInActive)
        val menuItemCount = typedArray.getInt(R.styleable.BottomViewDecoration_menuItemCount, 0)

        repeat(menuItemCount){
            addView(getStyledItem(colorSelected, colorInactive))
        }

        typedArray.recycle()

        setSelected(0)
    }

    private fun getStyledItem(colorSelected : Int, colorInactive: Int): BottomViewDecorationItem {
        val decorationItem = BottomViewDecorationItem(context)
        val param = LayoutParams(100, MATCH_PARENT, 1f)
        param.setMargins(6,0, 6, 0)
        with(decorationItem){
            layoutParams = param
            this.colorSelected = colorSelected
            colorInActive = colorInactive
        }
        return decorationItem
    }

    fun setSelected(itemNo : Int){
        if(itemNo >= childCount){
            Timber.e("Selected item number is out of range. item no: $itemNo childCount: $childCount")
            return
        }
        for((index, child) in children.withIndex()){
            (child as BottomViewDecorationItem).checked = index == itemNo
        }
    }

}