package com.canli.oya.traininventory.adapters;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.utils.GlideApp;

public class BindingAdapters {

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.gallery)
                .into(view);
    }
}
