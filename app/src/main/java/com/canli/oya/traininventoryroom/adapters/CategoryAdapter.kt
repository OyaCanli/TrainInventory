package com.canli.oya.traininventoryroom.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.CategoryItemBinding

class CategoryAdapter internal constructor(private val mClickListener: CategoryItemClickListener) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    var categoryList: List<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding = DataBindingUtil
                .inflate<CategoryItemBinding>(LayoutInflater.from(parent.context), R.layout.category_item,
                        parent, false)
        binding.categoryItemClick = mClickListener
        return CategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val currentCategory = categoryList?.get(position)
        holder.binding.categoryName = currentCategory
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return categoryList?.size ?: 0
    }

    inner class CategoryHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface CategoryItemClickListener {
        fun onCategoryItemClicked(categoryName: String)
    }
}
