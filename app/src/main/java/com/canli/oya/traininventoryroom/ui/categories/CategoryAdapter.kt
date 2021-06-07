package com.canli.oya.traininventoryroom.ui.categories

import android.content.Context
import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import com.canli.oya.traininventoryroom.ui.base.BaseListAdapter
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener

class CategoryAdapter (context: Context, clickListener: CategoryItemClickListener, swipeListener: SwipeDeleteListener<CategoryEntity>)
    : BaseListAdapter<CategoryEntity, CategoryItemClickListener>(context, clickListener, swipeListener){

    override fun getLayoutId(): Int = R.layout.item_category
}

interface CategoryItemClickListener {
    fun onCategoryItemClicked(view: View, category: CategoryEntity)
}
