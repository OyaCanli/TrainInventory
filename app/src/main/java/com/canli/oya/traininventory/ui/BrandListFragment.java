package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.BrandAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.viewmodel.ChosenTrainViewModel;
import com.canli.oya.traininventory.viewmodel.ChosenTrainViewModelFactory;

import java.util.LinkedList;
import java.util.List;

public class BrandListFragment extends Fragment{

    private List<BrandEntry> brands;
    private BrandAdapter adapter;
    private boolean undoClicked;
    private LinkedList<BrandEntry> tempListOfBrandsToErase = new LinkedList<>();
    private TrainDatabase mDb;

    public BrandListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        adapter = new BrandAdapter(getActivity());
        RecyclerView recycler = rootView.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);

        ChosenTrainViewModelFactory factory = new ChosenTrainViewModelFactory(mDb, 0);
        final ChosenTrainViewModel viewModel = ViewModelProviders.of(getActivity(), factory).get(ChosenTrainViewModel.class);
        viewModel.getBrandList().observe(getActivity(), new Observer<List<BrandEntry>>() {
            @Override
            public void onChanged(@Nullable List<BrandEntry> brandEntries) {
                Log.d("BrandListFragment", "" + brandEntries.size());
                adapter.setBrands(brandEntries);
                brands = brandEntries;
            }
        });

        getActivity().setTitle(getString(R.string.all_brands));

        final CoordinatorLayout coordinator = getActivity().findViewById(R.id.coordinator);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                //Remove from the adapter
                final int position = viewHolder.getAdapterPosition();
                final BrandEntry brandToErase = brands.get(position);
                brands.remove(position);
                adapter.setBrands(brands);
                adapter.notifyItemRemoved(position);
                tempListOfBrandsToErase.add(brandToErase);
                Snackbar snackbar = Snackbar
                        .make(coordinator, R.string.brand_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //If UNDO is clicked add the item back in the adapter
                                brands.add(position, brandToErase);
                                adapter.setBrands(brands);
                                adapter.notifyItemInserted(position);
                                tempListOfBrandsToErase.removeLast();
                            }
                        });
                snackbar.show();
            }
        }).attachToRecyclerView(recycler);

        return rootView;
    }

    private void deleteFromDatabase(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d("BrandListFragment", "delete is called");
                for(BrandEntry brand: tempListOfBrandsToErase){
                    mDb.brandDao().deleteBrand(brand);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        deleteFromDatabase();
    }
}

