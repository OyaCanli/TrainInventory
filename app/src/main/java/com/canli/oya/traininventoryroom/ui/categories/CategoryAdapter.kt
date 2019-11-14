package com.canli.oya.traininventoryroom.ui.categories

import android.content.Context
import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.BaseAdapter
import com.canli.oya.traininventoryroom.common.BaseDiffCallback
import com.canli.oya.traininventoryroom.common.SwipeDeleteListener
import com.canli.oya.traininventoryroom.data.CategoryEntry

class CategoryAdapter (context: Context, clickListener: CategoryItemClickListener, swipeListener: SwipeDeleteListener<CategoryEntry>)
    : BaseAdapter<CategoryEntry, CategoryItemClickListener>(context, clickListener, swipeListener){

    override fun getLayoutId(): Int = R.layout.item_category
}

interface CategoryItemClickListener {
    fun onCategoryItemClicked(view: View, category: CategoryEntry)
}

class CategoryDiffCallback: BaseDiffCallback<CategoryEntry>()
