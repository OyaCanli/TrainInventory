package com.canli.oya.traininventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.utils.GlideApp;

import java.util.List;

public class CustomSpinAdapter extends BaseAdapter {

    private Context mContext;
    private List<BrandEntry> mBrandList;

    public CustomSpinAdapter(Context context, List<BrandEntry> list) {
        mContext = context;
        mBrandList = list;
    }

    @Override
    public int getCount() {
        return mBrandList.size();
    }

    @Override
    public BrandEntry getItem(int position) {
        return mBrandList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.spinner_item, parent, false);
        }

        BrandEntry currentBrand = getItem(position);

        TextView brandName_tv = convertView.findViewById(R.id.spin_item_brand_name);
        ImageView logo_iv = convertView.findViewById(R.id.spin_item_logo);

        brandName_tv.setText(currentBrand.getBrandName());
        GlideApp.with(mContext)
                .load(currentBrand.getBrandLogoUri())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(logo_iv);

        return convertView;
    }
}
