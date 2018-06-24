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
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.utils.GlideApp;

import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder>{

    private List<TrainEntry> mTrainList;
    private final ListItemClickListener mClickListener;
    private final Context mContext;

    public TrainAdapter(@NonNull Context context, ListItemClickListener listener) {
        mClickListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_train, parent, false);
        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        TrainEntry currentTrain = mTrainList.get(position);

        holder.trainName_tv.setText(currentTrain.getTrainName());
        holder.brand_tv.setText(currentTrain.getBrandName());
        holder.reference_tv.setText(currentTrain.getModelReference());
        holder.category_tv.setText(mContext.getString(R.string.category_placeholder, currentTrain.getCategoryName()));

        GlideApp.with(mContext)
                .load(currentTrain.getImageUri())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.trainImage_iv);
    }

    @Override
    public int getItemCount() {
        if(mTrainList == null){
            return 0;
        }
        return mTrainList.size();
    }

    public void setTrains(List<TrainEntry> newList){
        mTrainList = newList;
        notifyDataSetChanged();
    }

    public class TrainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView trainName_tv;
        final TextView brand_tv;
        final TextView reference_tv;
        final TextView category_tv;
        final ImageView trainImage_iv;

        TrainViewHolder(View view){
            super(view);
            this.trainName_tv = view.findViewById(R.id.train_item_train_name);
            this.brand_tv = view.findViewById(R.id.train_item_brand);
            this.reference_tv = view.findViewById(R.id.train_item_reference);
            this.category_tv = view.findViewById(R.id.train_item_category);
            this.trainImage_iv = view.findViewById(R.id.product_item_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            mClickListener.onListItemClick(position);
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int position);
    }
}
