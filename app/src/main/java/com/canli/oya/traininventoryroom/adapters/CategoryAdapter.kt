package com.canli.oya.traininventoryroom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ItemCategoryBinding

class CategoryAdapter (private val mClickListener: CategoryItemClickListener) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    var categoryList: List<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding = DataBindingUtil
                .inflate<ItemCategoryBinding>(LayoutInflater.from(parent.context), R.layout.item_category,
                        parent, false)
        binding.categoryItemClick = mClickListener
        return CategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val currentCategory = categoryList?.get(position)
        holder.binding.categoryName = currentCategory
        holder.binding.executePendingBindings()
        holder.binding.categoryItemNumber.text = "${position+1}."
    }

    override fun getItemCount() = categoryList?.size ?: 0

    inner class CategoryHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    interface CategoryItemClickListener {
        fun onCategoryItemClicked(categoryName: String)
    }
}
