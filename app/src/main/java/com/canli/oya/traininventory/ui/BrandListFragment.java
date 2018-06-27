package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.BrandAdapter;
import com.canli.oya.traininventory.adapters.ListItemClickListener;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.util.List;

public class BrandListFragment extends Fragment implements ListItemClickListener{

    private List<BrandEntry> brands;
    private BrandAdapter adapter;
    private TrainDatabase mDb;
    private TextView empty_tv;
    private ImageView empty_image;
    private RecyclerView recycler;
    private MainViewModel viewModel;

    public BrandListFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_brandlist, container, false);
        setHasOptionsMenu(true);

        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        adapter = new BrandAdapter(getActivity(), this);
        recycler = rootView.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);

        empty_tv = rootView.findViewById(R.id.empty_text);
        empty_image = rootView.findViewById(R.id.empty_image);

        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getBrandList().observe(getActivity(), new Observer<List<BrandEntry>>() {
            @Override
            public void onChanged(@Nullable List<BrandEntry> brandEntries) {
                if (brandEntries.isEmpty()) {
                    showEmpty();
                } else {
                    adapter.setBrands(brandEntries);
                    brands = brandEntries;
                    showData();
                }
            }
        });

        getActivity().setTitle(getString(R.string.all_brands));

        final CoordinatorLayout coordinator = getActivity().findViewById(R.id.coordinator);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                //First take a backup of the brand to erase
                final BrandEntry brandToErase = brands.get(position);

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //Check whether this brand is used in trains table.
                        List<Integer> trainsThatUSeThisBrand = mDb.trainDao().getTrainsThatUseThisBrand(brandToErase.getBrandName());
                        if(trainsThatUSeThisBrand.size() > 0){
                            // If it is used, show a warning and don't let user delete this
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "This brand is used by some trains. You cannot erase this brand", Toast.LENGTH_LONG).show();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            return;
                        } else{
                            //If it is not used delete the brand
                            mDb.brandDao().deleteBrand(brandToErase);

                            //Show a snackbar for undoing delete
                            Snackbar snackbar = Snackbar
                                    .make(coordinator, R.string.brand_deleted, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //If UNDO is clicked, add the item back in the database
                                            mDb.brandDao().insertBrand(brandToErase);
                                        }
                                    });
                            snackbar.show();
                        }
                    }
                });
            }
        }).attachToRecyclerView(recycler);

        return rootView;
    }

    private void showEmpty() {
        recycler.setVisibility(View.GONE);
        empty_tv.setText(R.string.no_brands_found);
        empty_tv.setVisibility(View.VISIBLE);
        empty_image.setVisibility(View.VISIBLE);
    }

    private void showData() {
        if (empty_image.getVisibility() == View.VISIBLE) {
            empty_tv.setVisibility(View.GONE);
            empty_image.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    public void openAddBrandFragment() {
        AddBrandFragment addBrandFrag = new AddBrandFragment();
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addBrandFrag)
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            openAddBrandFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int position) {
        viewModel.setChosenBrand(brands.get(position));
        AddBrandFragment addBrandFrag = new AddBrandFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_REQUEST_CODE, Constants.EDIT_CASE);
        addBrandFrag.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addBrandFrag)
                .commit();
    }
}

