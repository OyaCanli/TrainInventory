package com.canli.oya.traininventoryroom.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.BR
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener
import com.canlioya.core.models.TrainMinimal

abstract class TrainBaseAdapter(private val clickListener: TrainItemClickListener) : ListAdapter<TrainMinimal, TrainBaseAdapter.ViewHolder>(
    TrainDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent, getLayoutId())

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentGrapheme = getItem(position)
        holder.bind(currentGrapheme, clickListener, position)
    }

    class ViewHolder private constructor(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentTrain: TrainMinimal, listener: TrainItemClickListener?, position: Int) {
            binding.setVariable(BR.item, currentTrain)
            binding.setVariable(BR.itemClick, listener)
            binding.setVariable(BR.position, position)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, @IdRes layoutId: Int): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater, layoutId, parent, false
                )
                return ViewHolder(binding)
            }
        }
    }

    abstract fun getLayoutId(): Int

    private class TrainDiffCallback : DiffUtil.ItemCallback<TrainMinimal>() {

        override fun areItemsTheSame(oldItem: TrainMinimal, newItem: TrainMinimal): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TrainMinimal, newItem: TrainMinimal): Boolean {
            return oldItem == newItem
        }
    }
}
