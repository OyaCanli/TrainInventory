package com.canli.oya.traininventory.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.CategoryEntry;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private List<String> mCategoryList;
    private CategoryItemClickListener mClickListener;

    public CategoryAdapter(CategoryItemClickListener listener){
        mClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
        return new CategoryHolder(view);
    }

    public void setCategories(List<String> newList){
        mCategoryList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        String currentCategory = mCategoryList.get(position);
        holder.categoryName_tv.setText(currentCategory);
    }

    @Override
    public int getItemCount() {
        return mCategoryList == null ? 0 : mCategoryList.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView categoryName_tv;
        final ImageView trainIcon_iv;

        CategoryHolder(View itemView) {
            super(itemView);
            categoryName_tv = itemView.findViewById(R.id.category_item_category_name);
            trainIcon_iv = itemView.findViewById(R.id.category_item_train_icon);
            trainIcon_iv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            mClickListener.onCategoryItemClicked(position);
        }
    }

    public interface CategoryItemClickListener{
        void onCategoryItemClicked(int position);
    }
}
