package com.canli.oya.traininventoryroom.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.BR


abstract class BaseAdapter<T, L>(private val itemClickListener : L?): PagedListAdapter<T, ItemViewHolder<T, L>>(BaseDiffCallback<T>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<T, L> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil
                .inflate<ViewDataBinding>(layoutInflater, getLayoutId(),
                        parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder<T, L>, position: Int) = holder.bind(getItem(position), itemClickListener)

    abstract fun getLayoutId() : Int
}

class ItemViewHolder<T, L> (val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(currentItem : T?, listener: L?){
        binding.setVariable(BR.item, currentItem)
        binding.setVariable(BR.itemClick, listener)
        binding.setVariable(BR.position, position)
        binding.executePendingBindings()
    }
}

open class BaseDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return false //TODO : to be overriden?
    }
}

interface SwipeDeleteListener {
    fun onDeleteConfirmed()
    fun onDeleteCanceled()
}