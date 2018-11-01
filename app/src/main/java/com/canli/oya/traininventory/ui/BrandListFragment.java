package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.BrandAdapter;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.databinding.FragmentBrandlistBinding;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.InjectorUtils;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.util.List;

public class BrandListFragment extends Fragment implements BrandAdapter.BrandItemClickListener{

    private List<BrandEntry> brands;
    private BrandAdapter adapter;
    private MainViewModel mViewModel;
    private FragmentBrandlistBinding binding;

    public BrandListFragment() {
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_brandlist, container, false);

        setHasOptionsMenu(true);

        adapter = new BrandAdapter(getActivity(), this);
        binding.included.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.included.list.setItemAnimator(new DefaultItemAnimator());
        binding.included.list.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mViewModel.loadBrandList(InjectorUtils.provideBrandRepo(getContext()));
        mViewModel.getBrandList().observe(BrandListFragment.this, new Observer<List<BrandEntry>>() {
            @Override
            public void onChanged(@Nullable List<BrandEntry> brandEntries) {
                if (brandEntries == null || brandEntries.isEmpty()) {
                    binding.included.setIsEmpty(true);
                    binding.included.setEmptyMessage(getString(R.string.no_brands_found));
                } else {
                    adapter.setBrands(brandEntries);
                    brands = brandEntries;
                    binding.included.setIsEmpty(false);
                }
            }
        });
        getActivity().setTitle(getString(R.string.all_brands));

        final CoordinatorLayout coordinator = getActivity().findViewById(R.id.coordinator);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                //First take a backup of the brand to erase
                final BrandEntry brandToErase = brands.get(position);

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //Check whether this brand is used in trains table.
                        if(mViewModel.isThisBrandUsed(brandToErase.getBrandName())){
                            // If it is used, show a warning and don't let the user delete this
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.cannot_erase_brand, Toast.LENGTH_LONG).show();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            //If it is not used delete the brand
                            mViewModel.deleteBrand(brandToErase);

                            //Show a snack bar for undoing delete
                            Snackbar snackbar = Snackbar
                                    .make(coordinator, R.string.brand_deleted, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mViewModel.insertBrand(brandToErase);
                                        }
                                    });
                            snackbar.show();
                        }
                    }
                });
            }
        }).attachToRecyclerView(binding.included.list);
    }

    private void openAddBrandFragment() {
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
    public void onBrandItemClicked(View view, BrandEntry clickedBrand) {
        switch(view.getId()){
            case R.id.brand_item_web_icon:{
                openWebSite(clickedBrand);
                break;
            }
            case R.id.brand_item_train_icon:{
                showTrainsFromThisBrand(clickedBrand);
                break;
            }
            case R.id.brand_item_edit_icon:{
                editBrand(clickedBrand);
                break;
            }
        }
    }

    private void editBrand(BrandEntry clickedBrand) {
        mViewModel.setChosenBrand(clickedBrand);
        AddBrandFragment addBrandFrag = new AddBrandFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_REQUEST_CODE, Constants.EDIT_CASE);
        addBrandFrag.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addBrandFrag)
                .commit();
    }

    private void showTrainsFromThisBrand(BrandEntry clickedBrand) {
        TrainListFragment trainListFrag = new TrainListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_REQUEST_CODE, Constants.TRAINS_OF_BRAND);
        args.putString(Constants.BRAND_NAME, clickedBrand.getBrandName());
        trainListFrag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, trainListFrag)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .commit();
    }

    private void openWebSite(BrandEntry clickedBrand){
        String urlString = clickedBrand.getWebUrl();
        Uri webUri = null;
        if(!TextUtils.isEmpty(urlString)){
            try{
                webUri = Uri.parse(urlString);
            }catch(Exception e){
                Log.e("BrandListFragment", e.toString());
            }
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(webUri);
            if(webIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(webIntent);
            }
        }
    }
}

