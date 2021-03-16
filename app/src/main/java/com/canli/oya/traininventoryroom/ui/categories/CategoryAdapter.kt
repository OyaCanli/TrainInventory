package com.canli.oya.traininventoryroom.ui.categories

import android.content.Context
import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.ui.base.BaseAdapter
import com.canli.oya.traininventoryroom.ui.base.BaseDiffCallback
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener

class CategoryAdapter (context: Context, clickListener: CategoryItemClickListener, swipeListener: SwipeDeleteListener<CategoryEntry>)
    : BaseAdapter<CategoryEntry, CategoryItemClickListener>(context, clickListener, swipeListener){

    override fun getLayoutId(): Int = R.layout.item_category
}

interface CategoryItemClickListener {
    fun onCategoryItemClicked(view: View, category: CategoryEntry)
}
