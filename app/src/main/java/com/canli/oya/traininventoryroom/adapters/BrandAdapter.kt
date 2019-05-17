package com.canli.oya.traininventoryroom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.databinding.ItemBrandBinding

class BrandAdapter(private val clickListener: BrandItemClickListener)
    : ListAdapter<BrandEntry, BrandAdapter.ViewHolder>(BrandDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), clickListener, position)

    class ViewHolder(val binding: ItemBrandBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentBrand: BrandEntry, listener : BrandItemClickListener, position : Int){
            binding.brand = currentBrand
            binding.brandItemClick = listener
            binding.brandItemNumber.text = "${position+1}."
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil
                        .inflate<ItemBrandBinding>(layoutInflater, R.layout.item_brand,
                                parent, false)
                return ViewHolder(binding)
            }
        }
    }

    interface BrandItemClickListener {
        fun onBrandItemClicked(view: View, clickedBrand: BrandEntry)
    }
}

class BrandDiffCallback : DiffUtil.ItemCallback<BrandEntry>() {
    override fun areItemsTheSame(oldItem: BrandEntry, newItem: BrandEntry): Boolean {
        return oldItem.brandId == newItem.brandId
    }

    override fun areContentsTheSame(oldItem: BrandEntry, newItem: BrandEntry): Boolean {
        return oldItem == newItem
    }
}
