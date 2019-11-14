package com.canli.oya.traininventoryroom.ui.brands

import android.content.Context
import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.BaseAdapter
import com.canli.oya.traininventoryroom.common.BaseDiffCallback
import com.canli.oya.traininventoryroom.common.SwipeDeleteListener
import com.canli.oya.traininventoryroom.data.BrandEntry

class BrandAdapter(context: Context, clickListener: BrandItemClickListener, swipeListener: SwipeDeleteListener<BrandEntry>)
    : BaseAdapter<BrandEntry, BrandItemClickListener>(context, clickListener, swipeListener) {

    override fun getLayoutId(): Int = R.layout.item_brand
}

interface BrandItemClickListener {
    fun onBrandItemClicked(view: View, clickedBrand: BrandEntry)
}

class BrandDiffCallback : BaseDiffCallback<BrandEntry>()
