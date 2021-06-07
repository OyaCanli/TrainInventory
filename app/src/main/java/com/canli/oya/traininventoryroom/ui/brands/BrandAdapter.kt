package com.canli.oya.traininventoryroom.ui.brands

import android.content.Context
import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.entities.BrandEntity
import com.canli.oya.traininventoryroom.ui.base.BaseListAdapter
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener

class BrandAdapter(context: Context, clickListener: BrandItemClickListener, swipeListener: SwipeDeleteListener<BrandEntity>)
    : BaseListAdapter<BrandEntity, BrandItemClickListener>(context, clickListener, swipeListener) {

    override fun getLayoutId(): Int = R.layout.item_brand
}

interface BrandItemClickListener {
    fun onBrandItemClicked(view: View, clickedBrand: BrandEntity)
}
