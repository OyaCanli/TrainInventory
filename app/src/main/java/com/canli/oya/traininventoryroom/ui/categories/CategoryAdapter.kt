package com.canli.oya.traininventoryroom.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ItemCategoryBinding

class CategoryAdapter (private val clickListener: CategoryItemClickListener) : ListAdapter<String, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), position, clickListener)

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentCategory: String?, position: Int, listener : CategoryItemClickListener) {
            binding.categoryName = currentCategory
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
        fun onCategoryItemClicked(categoryName: String)
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<String>(){
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
