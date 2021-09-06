package com.canli.oya.traininventoryroom.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.BR
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ItemConfirmDeleteBinding
import com.canli.oya.traininventoryroom.ui.common.ISwipeableAdapter
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.models.Train

const val VIEW_TYPE_NORMAL = 1
const val VIEW_TYPE_DELETE = 2

abstract class BaseListAdapter<T : Any, L>(val context: Context, private val itemClickListener: L?, private val swipeListener: SwipeDeleteListener<T>) :
    ListAdapter<T, RecyclerView.ViewHolder>(BaseDiffCallback<T>()), ISwipeableAdapter {

    private val swipedItems = mutableListOf<Int>()
    private var itemHeight = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_NORMAL) {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater, getLayoutId(), parent, false
            )
            ItemViewHolder<T, L>(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemConfirmDeleteBinding>(
                layoutInflater, R.layout.item_confirm_delete, parent, false
            )
            DeleteItemViewHolder<T>(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position) ?: return
        if (getItemViewType(position) == VIEW_TYPE_NORMAL) {
            (holder as ItemViewHolder<T, L>).bind(currentItem, itemClickListener, position)
        } else {
            if (itemHeight == 0) itemHeight = getItemHeightForType(currentItem, context)
            (holder as DeleteItemViewHolder<T>).bind(currentItem, swipeListener, position, itemHeight)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (swipedItems.contains(position)) VIEW_TYPE_DELETE
        else VIEW_TYPE_NORMAL
    }

    private fun getItemHeightForType(currentItem: T, context: Context): Int {
        val itemHeightRes = when (currentItem) {
            is Category -> R.dimen.category_item_height
            is Brand -> R.dimen.brand_item_height
            else -> R.dimen.train_item_height
        }
        return context.resources.getDimension(itemHeightRes).toInt()
    }

    override fun onItemSwiped(position: Int) {
        if (!swipedItems.contains(position)) {
            swipedItems.add(position)
        }
        notifyItemChanged(position)
    }

    fun cancelDelete(position: Int) {
        swipedItems.remove(position)
        notifyItemChanged(position)
    }

    fun itemDeleted(position: Int) {
        swipedItems.remove(position)
    }

    abstract fun getLayoutId(): Int
}

class ItemViewHolder<T, L>(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(currentItem: T, listener: L?, position: Int) {
        binding.setVariable(BR.item, currentItem)
        binding.setVariable(BR.itemClick, listener)
        binding.setVariable(BR.position, position)
        binding.executePendingBindings()
    }
}

class DeleteItemViewHolder<T>(val binding: ItemConfirmDeleteBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(currentItem: T, listener: SwipeDeleteListener<T>, position: Int, itemHeight: Int) {
        val params = binding.root.layoutParams
        params.height = itemHeight
        binding.root.layoutParams = params

        binding.confirmDeleteBtn.setOnClickListener {
            listener.onDeleteConfirmed(currentItem, position)
        }
        binding.cancelBtn.setOnClickListener {
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
        if (oldItem is Train && newItem is Train) {
            return oldItem as Train == newItem as Train
        }
        if (oldItem is Brand && newItem is Brand) {
            return oldItem as Brand == newItem as Brand
        }
        if (oldItem is Category && newItem is Category) {
            return oldItem as Category == newItem as Category
        }
        return false
    }
}

interface SwipeDeleteListener<T> {
    fun onDeleteConfirmed(itemToDelete: T, position: Int)
    fun onDeleteCanceled(position: Int)
}
