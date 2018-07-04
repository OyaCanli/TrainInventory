package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
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
import com.canli.oya.traininventory.adapters.CategoryAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.util.List;

public class CategoryListFragment extends Fragment implements CategoryAdapter.CategoryItemClickListener{

    private TrainDatabase mDb;
    private TextView empty_tv;
    private ImageView empty_image;
    private RecyclerView recycler;
    private CategoryAdapter mAdapter;
    private List<String> mCategories;

    public CategoryListFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_brandlist, container, false);
        setHasOptionsMenu(true);

        mAdapter = new CategoryAdapter(this);
        recycler = rootView.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(mAdapter);

        empty_tv = rootView.findViewById(R.id.empty_text);
        empty_image = rootView.findViewById(R.id.empty_image);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());
        final MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getCategoryList().observe(getActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> categoryEntries) {
                if (categoryEntries.isEmpty()) {
                    showEmpty();
                } else {
                    mAdapter.setCategories(categoryEntries);
                    mCategories = categoryEntries;
                    showData();
                }
            }
        });

        getActivity().setTitle(getString(R.string.all_categories));

        final CoordinatorLayout coordinator = getActivity().findViewById(R.id.coordinator);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                //First take a backup of the category to erase
                final CategoryEntry categoryToErase = new CategoryEntry(mCategories.get(position));

                //Remove the category from the database
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //First check whether this category is used by trains table
                        if (mDb.trainDao().isThisCategoryUsed(categoryToErase.getCategoryName())) {
                            // If it is used, show a warning and don't let user delete this
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.cannot_erase_category, Toast.LENGTH_LONG).show();
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                            return;
                        } else {
                            //If it is not used, erase the category
                            mDb.categoryDao().deleteCategory(categoryToErase);
                            //Show a snackbar for undoing delete
                            Snackbar snackbar = Snackbar
                                    .make(coordinator, R.string.brand_deleted, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //If UNDO is clicked, add the item back in the database
                                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mDb.categoryDao().insertCategory(categoryToErase);
                                                }
                                            });
                                        }
                                    });
                            snackbar.show();
                        }
                    }
                });
            }
        }).attachToRecyclerView(recycler);
    }

    private void showEmpty() {
        recycler.setVisibility(View.GONE);
        empty_tv.setText(R.string.no_categories_found);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            openAddCategoryFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddCategoryFragment() {
        AddCategoryFragment addCatFrag = new AddCategoryFragment();
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addCatFrag)
                .commit();
    }

    @Override
    public void onCategoryItemClicked(int position) {
        TrainListFragment trainListFrag = new TrainListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_REQUEST_CODE, Constants.TRAINS_OF_CATEGORY);
        args.putString(Constants.CATEGORY_NAME, mCategories.get(position));
        trainListFrag.setArguments(args);
        trainListFrag.setEnterTransition(new Slide(Gravity.END));
        trainListFrag.setExitTransition(new Slide(Gravity.START));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, trainListFrag)
                .addToBackStack(null)
                .commit();
    }
}
