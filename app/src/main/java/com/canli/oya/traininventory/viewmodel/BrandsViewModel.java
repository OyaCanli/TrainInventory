package com.canli.oya.traininventory.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.data.repositories.BrandRepository;

import java.util.List;

public class BrandsViewModel extends ViewModel {

    private final LiveData<List<BrandEntry>> brandList;
    private final MutableLiveData<BrandEntry> mChosenBrand = new MutableLiveData<>();
    private BrandRepository mRepo;

    BrandsViewModel(BrandRepository brandRepo) {
        brandList = brandRepo.getBrandList();
        mRepo = brandRepo;
    }

    public LiveData<List<BrandEntry>> getBrandList() {
        return brandList;
    }

    public LiveData<BrandEntry> getChosenBrand() {
        return mChosenBrand;
    }

    public void setChosenBrand(BrandEntry chosenBrand){
        mChosenBrand.setValue(chosenBrand);
    }

    public void insertBrand(BrandEntry brand){
        mRepo.insertBrand(brand);
    }

    public void deleteBrand(BrandEntry brand){
        mRepo.deleteBrand(brand);
    }

    public void updateBrand(BrandEntry brand) {
        mRepo.updateBrand(brand);
    }

    public boolean isThisBrandUsed(String brandName){
        return mRepo.isThisBrandUsed(brandName);
    }
}
