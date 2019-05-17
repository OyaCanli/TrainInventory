package com.canli.oya.traininventoryroom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.ItemTrainBinding

class TrainAdapter (private val clickListener: TrainItemClickListener) : ListAdapter<TrainEntry, TrainAdapter.ViewHolder>(TrainDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor (val binding: ItemTrainBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentTrain : TrainEntry, listener: TrainItemClickListener){
            binding.train = currentTrain
            binding.executePendingBindings()
            binding.trainItemClick = listener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil
                        .inflate<ItemTrainBinding>(layoutInflater, R.layout.item_train,
                                parent, false)
                return ViewHolder(binding)
            }
        }
    }

    interface TrainItemClickListener {
        fun onListItemClick(trainId: Int)
    }
}

class TrainDiffCallback : DiffUtil.ItemCallback<TrainEntry>() {

    override fun areItemsTheSame(oldItem: TrainEntry, newItem: TrainEntry): Boolean {
        return oldItem.trainId == newItem.trainId
    }

    override fun areContentsTheSame(oldItem: TrainEntry, newItem: TrainEntry): Boolean {
        return oldItem == newItem
    }

}
