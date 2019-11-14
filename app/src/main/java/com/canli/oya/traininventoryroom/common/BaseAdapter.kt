package com.canli.oya.traininventoryroom.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.BR
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ItemConfirmDeleteBinding

const val VIEW_TYPE_NORMAL = 1
const val VIEW_TYPE_DELETE = 2

abstract class BaseAdapter<T, L>(private val itemClickListener : L?, private val swipeListener: SwipeDeleteListener<T>): PagedListAdapter<T, RecyclerView.ViewHolder>(BaseDiffCallback<T>()) {

    private val swipedItems = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if(viewType == VIEW_TYPE_NORMAL){
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater, getLayoutId(), parent, false)
            ItemViewHolder<T, L>(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemConfirmDeleteBinding>(
                    layoutInflater, R.layout.item_confirm_delete, parent, false)
            DeleteItemViewHolder<T>(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        val currentItem = getItem(position) ?: return
        if(getItemViewType(position) == VIEW_TYPE_NORMAL){
            (holder as ItemViewHolder<T,L>).bind(currentItem, itemClickListener, position)
        } else {
            (holder as DeleteItemViewHolder<T>).bind(currentItem, swipeListener, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(swipedItems.contains(position)) VIEW_TYPE_DELETE
        else VIEW_TYPE_NORMAL
    }

    fun itemSwiped(position: Int) {
        if(!swipedItems.contains(position)){
            swipedItems.add(position)
        }
        notifyItemChanged(position)
    }

    fun cancelDelete(position: Int){
        swipedItems.remove(position)
        notifyItemChanged(position)
    }

    fun itemDeleted(position: Int){
        swipedItems.remove(position)
    }

    abstract fun getLayoutId() : Int

}

class ItemViewHolder<T, L> (val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(currentItem : T, listener: L?, position : Int){
        binding.setVariable(BR.item, currentItem)
        binding.setVariable(BR.itemClick, listener)
        binding.setVariable(BR.position, position)
        binding.executePendingBindings()
    }
}

class DeleteItemViewHolder<T> (val binding: ItemConfirmDeleteBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(currentItem : T, listener: SwipeDeleteListener<T>, position: Int){
        binding.confirmDeleteBtn.setOnClickListener{
            listener.onDeleteConfirmed(currentItem, position)
        }
        binding.cancelBtn.setOnClickListener{
            listener.onDeleteCanceled(position)
        }
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

interface SwipeDeleteListener<T> {
    fun onDeleteConfirmed(itemToDelete : T, position : Int)
    fun onDeleteCanceled(position: Int)
}

