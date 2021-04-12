package com.canli.oya.traininventoryroom.ui.searchtrain

import android.content.Context
import android.widget.ArrayAdapter


class FilterBySpinnerAdapter (context : Context,
                              private var list : List<String> = arrayListOf(),
                              private val filterByText : String)
    : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list) {


    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        insert(filterByText, 0)
    }

    fun setCategories(newList: ArrayList<String>){
        list = newList
        insert(filterByText, 0)
        notifyDataSetChanged()
    }
}