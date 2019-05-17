package com.canli.oya.traininventoryroom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.ItemTrainBinding

class TrainAdapter (private val mClickListener: TrainItemClickListener) : RecyclerView.Adapter<TrainAdapter.TrainViewHolder>() {

    var trainList: List<TrainEntry>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val binding = DataBindingUtil
                .inflate<ItemTrainBinding>(LayoutInflater.from(parent.context), R.layout.item_train,
                        parent, false)
        binding.trainItemClick = mClickListener
        return TrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        val currentTrain = trainList?.get(position)
        holder.binding.train = currentTrain
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = trainList?.size ?: 0

    inner class TrainViewHolder(val binding: ItemTrainBinding) : RecyclerView.ViewHolder(binding.root)

    interface TrainItemClickListener {
        fun onListItemClick(trainId: Int)
    }
}
