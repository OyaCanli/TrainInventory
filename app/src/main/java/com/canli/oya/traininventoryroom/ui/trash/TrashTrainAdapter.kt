package com.canli.oya.traininventoryroom.ui.trash

import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.ui.base.BaseTrainAdapter
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener

class TrashTrainAdapter(clickListener : TrainItemClickListener) : BaseTrainAdapter(clickListener) {

    override fun getLayoutId(): Int = R.layout.item_trash
}