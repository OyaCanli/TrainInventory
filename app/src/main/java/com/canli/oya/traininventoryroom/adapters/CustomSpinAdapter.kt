package com.canli.oya.traininventoryroom.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.utils.GlideApp

class CustomSpinAdapter(private val mContext: Context, var mBrandList: List<BrandEntry>?) : BaseAdapter() {

    override fun getCount(): Int {
        return mBrandList?.size ?: 0
    }

    override fun getItem(position: Int): BrandEntry? {
        return mBrandList?.get(position)
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
        var convertView = view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(resource, parent, false)
        }

        val currentBrand = getItem(position)

        val brandName = convertView!!.findViewById<TextView>(R.id.spin_item_brand_name)
        val logo = convertView.findViewById<ImageView>(R.id.spin_item_logo)

        brandName.text = currentBrand?.brandName
        val imageUri = currentBrand?.brandLogoUri
        if (imageUri == null) {
            logo.visibility = View.GONE
        } else {
            logo.visibility = View.VISIBLE
            GlideApp.with(mContext)
                    .load(imageUri)
                    .centerCrop()
                    .into(logo)
        }

        return convertView
    }
}
