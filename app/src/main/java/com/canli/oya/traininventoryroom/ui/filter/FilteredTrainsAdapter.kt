package com.canli.oya.traininventoryroom.ui.filter

import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.ui.base.TrainBaseAdapter
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener

class FilteredTrainsAdapter(clickListener : TrainItemClickListener) : TrainBaseAdapter(clickListener) {

    override fun getLayoutId(): Int = R.layout.item_train

}