package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import android.util.Log;

import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.data.repositories.BrandRepository;
import com.canli.oya.traininventory.data.repositories.CategoryRepository;
import com.canli.oya.traininventory.data.repositories.TrainRepository;
import com.canli.oya.traininventory.ui.AddTrainFragment;
import com.canli.oya.traininventory.ui.BrandListFragment;
import com.canli.oya.traininventory.ui.CategoryListFragment;
import com.canli.oya.traininventory.ui.TrainDetailsFragment;
import com.canli.oya.traininventory.ui.TrainListFragment;

import java.util.LinkedList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private LiveData<List<TrainEntry>> mTrainList;
    private LiveData<List<BrandEntry>> mBrandList;
    private LiveData<List<String>> mCategoryList;
    private TrainRepository mTrainRepo;
    private BrandRepository mBrandRepo;
    private CategoryRepository mCategoryRepo;
    private final MutableLiveData<BrandEntry> mChosenBrand = new MutableLiveData<>();

    private static final String TAG = "MainViewModel";

    /////////// TRAIN LIST /////////////
    public void loadTrainList(TrainRepository trainRepo){
        if(mTrainList == null){
            mTrainList = trainRepo.getTrainList();
        }
        mTrainRepo = trainRepo;
    }

    public LiveData<List<TrainEntry>> getTrainList() {
        return mTrainList;
    }

    public void insertTrain(TrainEntry train){
        mTrainRepo.insertTrain(train);
    }

    public void updateTrain(TrainEntry train){
        mTrainRepo.updateTrain(train);
    }

    public void deleteTrain(TrainEntry train){
        mTrainRepo.deleteTrain(train);
    }

    ////////////// BRAND LIST //////////////////

    public void loadBrandList(BrandRepository brandRepo){
        if(mBrandList == null){
            mBrandList = brandRepo.getBrandList();
        }
        mBrandRepo = brandRepo;
    }

    public LiveData<List<BrandEntry>> getBrandList() {
        return mBrandList;
    }

    public LiveData<BrandEntry> getChosenBrand() {
        return mChosenBrand;
    }

    public void setChosenBrand(BrandEntry chosenBrand){
        mChosenBrand.setValue(chosenBrand);
    }

    public void insertBrand(BrandEntry brand){
        mBrandRepo.insertBrand(brand);
    }

    public void deleteBrand(BrandEntry brand){
        mBrandRepo.deleteBrand(brand);
    }

    public void updateBrand(BrandEntry brand) {
        mBrandRepo.updateBrand(brand);
    }

    public boolean isThisBrandUsed(String brandName){
        return mBrandRepo.isThisBrandUsed(brandName);
    }

    //////////////// CATEGORY LIST //////////////////

    public void loadCategoryList(CategoryRepository categoryRepo){
        if(mCategoryList == null){
            mCategoryList = categoryRepo.getCategoryList();
        }
        mCategoryRepo = categoryRepo;
    }

    public LiveData<List<String>> getCategoryList() {
        return mCategoryList;
    }

    public void deleteCategory(CategoryEntry category){
        mCategoryRepo.deleteCategory(category);
    }

    public void insertCategory(CategoryEntry category){
        mCategoryRepo.insertCategory(category);
    }

    public boolean isThisCategoryUsed(String category){
        return mCategoryRepo.isThisCategoryUsed(category);
    }

    ///////////// SEARCH //////////////////////////
    public LiveData<List<TrainEntry>> getTrainsFromThisBrand(String brandName){
        return mTrainRepo.getTrainsFromThisBrand(brandName);
    }

    public LiveData<List<TrainEntry>> getTrainsFromThisCategory(String category){
        return mTrainRepo.getTrainsFromThisCategory(category);
    }

    public List<TrainEntry> searchInTrains(String query){
        return mTrainRepo.searchInTrains(query);
    }


}
