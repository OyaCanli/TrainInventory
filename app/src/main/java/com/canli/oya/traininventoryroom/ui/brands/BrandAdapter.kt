package com.canli.oya.traininventoryroom.ui.brands

import android.content.Context
import android.view.View
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.ui.base.BaseListAdapter
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener
import com.canlioya.core.models.Brand

class BrandAdapter(context: Context, clickListener: BrandItemClickListener, swipeListener: SwipeDeleteListener<Brand>) :
    BaseListAdapter<Brand, BrandItemClickListener>(context, clickListener, swipeListener) {

    override fun getLayoutId(): Int = R.layout.item_brand
}

interface BrandItemClickListener {
    fun onBrandItemClicked(view: View, clickedBrand: Brand)
}
