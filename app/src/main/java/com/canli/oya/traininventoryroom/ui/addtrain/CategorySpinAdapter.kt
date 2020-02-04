package com.canli.oya.traininventoryroom.ui.addtrain

import android.content.Context
import android.widget.ArrayAdapter
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry

class CategorySpinAdapter(context : Context,
                          val categoryList : ArrayList<String> = arrayListOf() )
    : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categoryList) {

    private val selectText : String = context.getString(R.string.select_category) ?: "--Select category--"

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        insert(selectText, 0)
    }

    fun setCategories(newList: PagedList<CategoryEntry>){
        categoryList.clear()
        categoryList.add(selectText)
        categoryList.addAll(newList.map { categoryEntry -> categoryEntry.categoryName })
        notifyDataSetChanged()
    }
}