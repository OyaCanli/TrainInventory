package com.canli.oya.traininventoryroom.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.ItemCategoryBinding

class CategoryAdapter (private val clickListener: CategoryItemClickListener) : ListAdapter<CategoryEntry, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), position, clickListener)

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentCategory: CategoryEntry, position: Int, listener : CategoryItemClickListener) {
            binding.category = currentCategory
            binding.categoryItemNumber.text = "${position + 1}."
            binding.categoryItemClick = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemCategoryBinding>(layoutInflater, R.layout.item_category,
                                parent, false)
                return ViewHolder(binding)
            }
        }
    }

    interface CategoryItemClickListener {
        fun onCategoryItemClicked(view: View, category: CategoryEntry)
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryEntry>(){
    override fun areItemsTheSame(oldItem: CategoryEntry, newItem: CategoryEntry): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CategoryEntry, newItem: CategoryEntry): Boolean {
        return oldItem == newItem
    }
}
