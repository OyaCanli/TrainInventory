package com.canli.oya.traininventoryroom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.TrainItemBinding

class TrainAdapter internal constructor(private val mClickListener: TrainItemClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<TrainAdapter.TrainViewHolder>() {

    var trainList: List<TrainEntry>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val binding = DataBindingUtil
                .inflate<TrainItemBinding>(LayoutInflater.from(parent.context), R.layout.train_item,
                        parent, false)
        binding.trainItemClick = mClickListener
        return TrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        val currentTrain = trainList?.get(position)
        holder.binding.train = currentTrain
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return trainList?.size ?: 0
    }

    inner class TrainViewHolder(val binding: TrainItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    interface TrainItemClickListener {
        fun onListItemClick(trainId: Int)
    }
}
