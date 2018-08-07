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
        binding.setBrandItemClick(mClickListener);
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
    }

    @Override
    public int getItemCount() {
        return mBrandList == null ? 0 : mBrandList.size();
    }

    class BrandViewHolder extends RecyclerView.ViewHolder {

        final BrandItemBinding binding;

        BrandViewHolder(BrandItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface BrandItemClickListener{
        void onBrandItemClicked(View view, BrandEntry clickedBrand);
    }
}
