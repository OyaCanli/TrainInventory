package com.canli.oya.traininventoryroom.ui.trains

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.databinding.ItemTrainBinding

class TrainAdapter (private val clickListener: TrainItemClickListener) : PagedListAdapter<TrainMinimal, TrainAdapter.ViewHolder>(TrainDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor (val binding: ItemTrainBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentTrain : TrainMinimal?, listener: TrainItemClickListener){
            currentTrain?.let {
                binding.train = it
                binding.executePendingBindings()
                binding.trainItemClick = listener
            }
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

class TrainDiffCallback : DiffUtil.ItemCallback<TrainMinimal>() {

    override fun areItemsTheSame(oldItem: TrainMinimal, newItem: TrainMinimal): Boolean {
        return oldItem.trainId == newItem.trainId
    }

    override fun areContentsTheSame(oldItem: TrainMinimal, newItem: TrainMinimal): Boolean {
        return oldItem == newItem
    }

}
