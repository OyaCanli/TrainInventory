package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.BrandAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.util.LinkedList;
import java.util.List;

public class BrandListFragment extends Fragment {

    private List<BrandEntry> brands;
    private BrandAdapter adapter;
    private final LinkedList<BrandEntry> tempListOfBrandsToErase = new LinkedList<>();
    private TrainDatabase mDb;
    private TextView empty_tv;
    private ImageView empty_image;
    private RecyclerView recycler;

    public BrandListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_brandlist, container, false);

        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        adapter = new BrandAdapter(getActivity());
        recycler = rootView.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);

        empty_tv = rootView.findViewById(R.id.empty_text);
        empty_image = rootView.findViewById(R.id.empty_image);

        final MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getBrandList().observe(getActivity(), new Observer<List<BrandEntry>>() {
            @Override
            public void onChanged(@Nullable List<BrandEntry> brandEntries) {
                if(brandEntries.isEmpty()){
                    showEmpty();
                } else{
                    adapter.setBrands(brandEntries);
                    brands = brandEntries;
                    showData();
                }
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
                final int position = viewHolder.getAdapterPosition();

                //First take a backup of the brand to erase
                final BrandEntry brandToErase = brands.get(position);

                //Remove only from the adapter
                brands.remove(position);
                adapter.setBrands(brands);
                adapter.notifyItemRemoved(position);

                //Add it in a linked list
                tempListOfBrandsToErase.add(brandToErase);

                //Show a snackbar for undoing delete
                Snackbar snackbar = Snackbar
                        .make(coordinator, R.string.brand_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                /*If UNDO is clicked, add the item back in the adapter
                                and remove it from the list of brands to erase*/
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

    private void showEmpty(){
        recycler.setVisibility(View.GONE);
        empty_tv.setText(R.string.no_brands_found);
        empty_tv.setVisibility(View.VISIBLE);
        empty_image.setVisibility(View.VISIBLE);
    }

    private void showData(){
        if(empty_image.getVisibility() == View.VISIBLE){
            empty_tv.setVisibility(View.GONE);
            empty_image.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
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

    public void openAddBrandFragment() {
        AddBrandFragment addBrandFrag = new AddBrandFragment();
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addBrandFrag)
                .commit();
    }
}

