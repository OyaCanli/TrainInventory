package com.canli.oya.traininventoryroom.ui.trash

import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.ui.base.TrainBaseAdapter
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener

class TrashTrainAdapter(clickListener : TrainItemClickListener) : TrainBaseAdapter(clickListener) {

    override fun getLayoutId(): Int = R.layout.item_trash
}