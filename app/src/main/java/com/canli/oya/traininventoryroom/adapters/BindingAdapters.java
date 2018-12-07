package com.canli.oya.traininventoryroom.adapters;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.canli.oya.traininventoryroom.R;
import com.canli.oya.traininventoryroom.utils.GlideApp;

public class BindingAdapters {

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_gallery)
                .into(view);
    }
}
