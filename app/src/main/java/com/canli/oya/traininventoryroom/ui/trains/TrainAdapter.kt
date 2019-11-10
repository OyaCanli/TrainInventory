package com.canli.oya.traininventoryroom.ui.trains

import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.BaseAdapter
import com.canli.oya.traininventoryroom.common.BaseDiffCallback
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal

class TrainAdapter (clickListener: TrainItemClickListener) : BaseAdapter<TrainMinimal, TrainItemClickListener>(clickListener) {
    override fun getLayoutId(): Int  = R.layout.item_train
}

interface TrainItemClickListener {
    fun onListItemClick(trainId: Int)
}

class TrainDiffCallback : BaseDiffCallback<TrainEntry>()
