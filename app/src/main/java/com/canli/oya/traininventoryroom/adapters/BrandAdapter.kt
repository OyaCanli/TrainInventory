package com.canli.oya.traininventoryroom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.databinding.ItemBrandBinding

class BrandAdapter internal constructor(
        private val mClickListener: BrandItemClickListener)
    : androidx.recyclerview.widget.RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {

    var brandList: List<BrandEntry>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding = DataBindingUtil
                .inflate<ItemBrandBinding>(LayoutInflater.from(parent.context), R.layout.item_brand,
                        parent, false)
        binding.brandItemClick = mClickListener
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val currentBrand = brandList?.get(position)
        holder.binding.brand = currentBrand
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return brandList?.size ?: 0
    }

    inner class BrandViewHolder(val binding: ItemBrandBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    interface BrandItemClickListener {
        fun onBrandItemClicked(view: View, clickedBrand: BrandEntry)
    }
}
