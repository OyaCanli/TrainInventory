package com.canli.oya.traininventoryroom.ui.brands

import android.content.Context
import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.ui.base.BaseAdapter
import com.canli.oya.traininventoryroom.ui.base.BaseDiffCallback
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener

class BrandAdapter(context: Context, clickListener: BrandItemClickListener, swipeListener: SwipeDeleteListener<BrandEntry>)
    : BaseAdapter<BrandEntry, BrandItemClickListener>(context, clickListener, swipeListener) {

    override fun getLayoutId(): Int = R.layout.item_brand
}

interface BrandItemClickListener {
    fun onBrandItemClicked(view: View, clickedBrand: BrandEntry)
}
