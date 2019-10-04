package com.canli.oya.traininventoryroom.ui.addtrain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.GlideApp
import com.canli.oya.traininventoryroom.data.BrandEntry

class CustomSpinAdapter(private val mContext: Context, var mBrandList: List<BrandEntry>?) : BaseAdapter() {

    override fun getCount(): Int {
        return mBrandList?.size?.plus(1) ?: 1
    }

    override fun getItem(position: Int): BrandEntry? {
        return if(position == 0) null else mBrandList?.get(position-1)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        return createViewFromResource(view, parent, position, R.layout.item_spinner)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(convertView, parent, position, R.layout.item_spinner_dropdown)
    }

    private fun createViewFromResource(view: View?, parent: ViewGroup, position: Int, resource : Int): View {
        val convertView = view ?: LayoutInflater.from(mContext).inflate(resource, parent, false)

        val brandNameTv = convertView.findViewById<TextView>(R.id.spin_item_brand_name)
        val logoImage = convertView.findViewById<ImageView>(R.id.spin_item_logo)

        val currentBrand = getItem(position)

        if(currentBrand == null){
            brandNameTv.text = mContext.getString(R.string.select_brand)
            logoImage.visibility = View.GONE
        } else {
            brandNameTv.text = currentBrand.brandName
            val imageUri = currentBrand.brandLogoUri
            if (imageUri == null) {
                logoImage.visibility = View.GONE
            } else {
                logoImage.visibility = View.VISIBLE
                GlideApp.with(mContext)
                        .load(imageUri)
                        .centerCrop()
                        .into(logoImage)
            }
        }

        return convertView
    }
}
