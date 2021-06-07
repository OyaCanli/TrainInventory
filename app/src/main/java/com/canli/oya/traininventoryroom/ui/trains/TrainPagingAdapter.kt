package com.canli.oya.traininventoryroom.ui.trains

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ItemConfirmDeleteBinding
import com.canli.oya.traininventoryroom.databinding.ItemTrainBinding
import com.canli.oya.traininventoryroom.ui.base.DeleteItemViewHolder
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener
import com.canli.oya.traininventoryroom.ui.base.VIEW_TYPE_DELETE
import com.canli.oya.traininventoryroom.ui.base.VIEW_TYPE_NORMAL
import com.canli.oya.traininventoryroom.ui.common.ISwipeableAdapter
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener
import com.canlioya.core.models.TrainMinimal

class TrainPagingAdapter(
    private val context: Context,
    private val clickListener: TrainItemClickListener,
    private val swipeListener: SwipeDeleteListener<TrainMinimal>
) : PagingDataAdapter<TrainMinimal, RecyclerView.ViewHolder>(TrainDiffCallback()),
    ISwipeableAdapter {

    private val swipedItems = mutableListOf<Int>()
    private var itemHeight = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_NORMAL) {
            val binding = DataBindingUtil.inflate<ItemTrainBinding>(
                layoutInflater, R.layout.item_train, parent, false
            )
            ItemViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemConfirmDeleteBinding>(
                layoutInflater, R.layout.item_confirm_delete, parent, false
            )
            DeleteItemViewHolder<TrainMinimal>(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position) ?: return
        if (getItemViewType(position) == VIEW_TYPE_NORMAL) {
            (holder as ItemViewHolder).bind(currentItem, clickListener, position)
        } else {
            if (itemHeight == 0) itemHeight = context.resources.getDimension(R.dimen.train_item_height).toInt()
            (holder as DeleteItemViewHolder<TrainMinimal>).bind(
                currentItem,
                swipeListener,
                position,
                itemHeight
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (swipedItems.contains(position)) VIEW_TYPE_DELETE
        else VIEW_TYPE_NORMAL
    }

    fun cancelDelete(position: Int) {
        swipedItems.remove(position)
        notifyItemChanged(position)
    }

    fun itemDeleted(position: Int) {
        swipedItems.remove(position)
    }

    override fun onItemSwiped(position: Int) {
        if (!swipedItems.contains(position)) {
            swipedItems.add(position)
        }
        notifyItemChanged(position)
    }
}

class ItemViewHolder(val binding: ItemTrainBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(currentItem: TrainMinimal, listener: TrainItemClickListener?, position: Int) {
        binding.item = currentItem
        binding.itemClick = listener
        binding.position = position
        binding.executePendingBindings()
    }
}

class TrainDiffCallback : DiffUtil.ItemCallback<TrainMinimal>() {
    override fun areItemsTheSame(oldItem: TrainMinimal, newItem: TrainMinimal): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: TrainMinimal, newItem: TrainMinimal): Boolean {
        return oldItem == newItem
    }
}
