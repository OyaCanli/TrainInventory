package com.canli.oya.traininventoryroom.ui.brands

import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.BaseAdapter
import com.canli.oya.traininventoryroom.common.BaseDiffCallback
import com.canli.oya.traininventoryroom.data.BrandEntry

class BrandAdapter(clickListener: BrandItemClickListener) : BaseAdapter<BrandEntry, BrandItemClickListener>(clickListener) {
    override fun getLayoutId(): Int = R.layout.item_brand
}

interface BrandItemClickListener {
    fun onBrandItemClicked(view: View, clickedBrand: BrandEntry)
}

class BrandDiffCallback : BaseDiffCallback<BrandEntry>()
