package com.canli.oya.traininventory.data.repositories;

import android.arch.lifecycle.LiveData;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.utils.AppExecutors;

import java.util.List;

public class BrandRepository {

    private static BrandRepository sInstance;
    private final TrainDatabase mDatabase;
    private final LiveData<List<BrandEntry>> brandList;
    private final AppExecutors mExecutors;

    private BrandRepository(TrainDatabase database, AppExecutors executors) {
        mDatabase = database;
        brandList = loadBrands();
        mExecutors = executors;
    }

    public static BrandRepository getInstance(TrainDatabase database, AppExecutors executors){
        if (sInstance == null) {
            synchronized (BrandRepository.class) {
                sInstance = new BrandRepository(database, executors);
            }
        }
        return sInstance;
    }

    private LiveData<List<BrandEntry>> loadBrands(){
        return mDatabase.brandDao().getAllBrands();
    }

    public LiveData<List<BrandEntry>> getBrandList() {
        return brandList;
    }

    public void insertBrand(final BrandEntry brand){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.brandDao().insertBrand(brand);
            }
        });
    }

    public void updateBrand(final BrandEntry brand){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.brandDao().updateBrandInfo(brand);
            }
        });
    }

    public void deleteBrand(final BrandEntry brand){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.brandDao().deleteBrand(brand);
            }
        });
    }

    public boolean isThisBrandUsed(final String brandName){
        return mDatabase.trainDao().isThisBrandUsed(brandName);
    }
}
