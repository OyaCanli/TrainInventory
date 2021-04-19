package com.canli.oya.traininventoryroom.ui.trash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.databinding.ItemTrashBinding

class TrashAdapter (private val clickListener : TrashItemClickListener) : ListAdapter<TrainMinimal, TrashAdapter.ViewHolder>(TrainDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentGrapheme = getItem(position)
        holder.bind(currentGrapheme, clickListener, position)
    }

    class ViewHolder private constructor(val binding: ItemTrashBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentTrain: TrainMinimal, listener: TrashItemClickListener?, position: Int){
            binding.item = currentTrain
            binding.itemClick = listener
            binding.position = position
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTrashBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    private class TrainDiffCallback : DiffUtil.ItemCallback<TrainMinimal>() {

        override fun areItemsTheSame(oldItem: TrainMinimal, newItem: TrainMinimal): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TrainMinimal, newItem: TrainMinimal): Boolean {
            return oldItem == newItem
        }
    }
}

interface TrashItemClickListener {
    fun onRestoreClicked(trainId : Int)
    fun onDeleteClicked(trainId : Int)
}