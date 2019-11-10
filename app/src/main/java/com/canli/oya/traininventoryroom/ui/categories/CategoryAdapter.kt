package com.canli.oya.traininventoryroom.ui.categories

import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.BaseAdapter
import com.canli.oya.traininventoryroom.common.BaseDiffCallback
import com.canli.oya.traininventoryroom.data.CategoryEntry

class CategoryAdapter (clickListener: CategoryItemClickListener) : BaseAdapter<CategoryEntry, CategoryItemClickListener>(clickListener){
    override fun getLayoutId(): Int = R.layout.item_category

    /*val swipedItems : MutableList<CategoryEntry> = ArrayList()

    fun cancelDelete(item: CategoryEntry){
        swipedItems.remove(item)
        notifyDataSetChanged()
    }

    fun itemSwiped(position: Int) {
        Timber.d("ITem changed at position $position")
        val currentItem = getItem(position)
        if(!swipedItems.contains(currentItem)){
            swipedItems.add(currentItem)
        }
        notifyItemChanged(position)
    }*/

}

interface CategoryItemClickListener {
    fun onCategoryItemClicked(view: View, category: CategoryEntry)
}

class CategoryDiffCallback: BaseDiffCallback<CategoryEntry>()
