package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.databinding.FragmentAddCategoryBinding;
import com.canli.oya.traininventory.utils.InjectorUtils;
import com.canli.oya.traininventory.viewmodel.CategoryViewModel;
import com.canli.oya.traininventory.viewmodel.CategoryViewModelFactory;


public class AddCategoryFragment extends Fragment {

    private FragmentAddCategoryBinding binding;
    private CategoryViewModel viewModel;

    public AddCategoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_category, container, false);

        binding.addCategoryEditCatName.requestFocus();
        binding.addCategorySaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CategoryViewModelFactory factory = InjectorUtils.provideCategoryVMFactory(getActivity());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(CategoryViewModel.class);
    }

    private void saveCategory() {
        String categoryName = binding.addCategoryEditCatName.getText().toString().trim();
        final CategoryEntry newCategory = new CategoryEntry(categoryName);
        //Insert the category by the intermediance of view model
        viewModel.insertCategory(newCategory);

        //Remove the fragment
        Fragment parentFrag = getParentFragment();
        Fragment currentInstance;
        if (parentFrag instanceof AddTrainFragment) {
            currentInstance = getFragmentManager().findFragmentById(R.id.childFragContainer);
        } else {
            currentInstance = getFragmentManager().findFragmentById(R.id.brandlist_addFrag_container);
        }

        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(currentInstance)
                .commit();
    }
}
