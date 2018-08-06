package com.canli.oya.traininventory.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.databinding.TrainItemBinding;
import com.canli.oya.traininventory.utils.GlideApp;

import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder>{

    private List<TrainEntry> mTrainList;
    private final TrainItemClickListener mClickListener;
    private final Context mContext;

    public TrainAdapter(@NonNull Context context, TrainItemClickListener listener) {
        mClickListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TrainItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.train_item,
                        parent, false);
        return new TrainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        TrainEntry currentTrain = mTrainList.get(position);

        holder.binding.trainItemTrainName.setText(currentTrain.getTrainName());
        holder.binding.trainItemBrand.setText(currentTrain.getBrandName());
        holder.binding.trainItemReference.setText(currentTrain.getModelReference());
        holder.binding.trainItemCategory.setText(mContext.getString(R.string.category_placeholder, currentTrain.getCategoryName()));

        GlideApp.with(mContext)
                .load(currentTrain.getImageUri())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.productItemImage);
    }

    @Override
    public int getItemCount() {
        return mTrainList == null ? 0 : mTrainList.size();
    }

    public void setTrains(List<TrainEntry> newList){
        mTrainList = newList;
        notifyDataSetChanged();
    }

    public class TrainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TrainItemBinding binding;

        TrainViewHolder(TrainItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int trainId = mTrainList.get(getLayoutPosition()).getTrainId();
            mClickListener.onListItemClick(trainId);
        }
    }

    public interface TrainItemClickListener {
        void onListItemClick(int trainId);
    }
}
