package com.canli.oya.traininventory.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.databinding.BrandItemBinding;
import com.canli.oya.traininventory.utils.GlideApp;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder>{

    private List<BrandEntry> mBrandList;
    private final Context mContext;
    private final BrandItemClickListener mClickListener;

    public BrandAdapter(Context context, BrandItemClickListener listener){
        mContext = context;
        mClickListener = listener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BrandItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.brand_item,
                        parent, false);
        return new BrandViewHolder(binding);
    }

    public void setBrands(List<BrandEntry> newList){
        mBrandList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        BrandEntry currentBrand = mBrandList.get(position);
        holder.binding.setBrand(currentBrand);
        holder.binding.executePendingBindings();
        GlideApp.with(mContext)
                .load(currentBrand.getBrandLogoUri())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.brandItemLogo);
    }

    @Override
    public int getItemCount() {
        return mBrandList == null ? 0 : mBrandList.size();
    }

    public class BrandViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final BrandItemBinding binding;

        BrandViewHolder(BrandItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.brandItemWebIcon.setOnClickListener(this);
            binding.brandItemTrainIcon.setOnClickListener(this);
            binding.brandItemEditIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            mClickListener.onBrandItemClicked(v, position);
        }
    }

    public interface BrandItemClickListener{
        void onBrandItemClicked(View view, int position);
    }
}
