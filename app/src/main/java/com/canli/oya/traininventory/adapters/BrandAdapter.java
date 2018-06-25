package com.canli.oya.traininventory.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder>{

    private List<BrandEntry> mBrandList;
    private final Context mContext;

    public BrandAdapter(Context context){
        mContext = context;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    public void setBrands(List<BrandEntry> newList){
        mBrandList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        BrandEntry currentBrand = mBrandList.get(position);

        holder.brandName_tv.setText(currentBrand.getBrandName());
        GlideApp.with(mContext)
                .load(currentBrand.getBrandLogoUri())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.logo_iv);
    }

    @Override
    public int getItemCount() {
        return mBrandList == null ? 0 : mBrandList.size();
    }

    public class BrandViewHolder extends RecyclerView.ViewHolder{

        final TextView brandName_tv;
        final ImageView logo_iv;

        BrandViewHolder(View itemView) {
            super(itemView);
            brandName_tv = itemView.findViewById(R.id.brand_item_brandName);
            logo_iv = itemView.findViewById(R.id.brand_item_logo);
        }
    }
}
