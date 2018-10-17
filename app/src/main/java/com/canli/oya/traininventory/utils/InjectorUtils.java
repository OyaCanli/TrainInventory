package com.canli.oya.traininventory.utils;

import android.content.Context;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.repositories.BrandRepository;
import com.canli.oya.traininventory.data.repositories.CategoryRepository;
import com.canli.oya.traininventory.data.repositories.TrainRepository;
import com.canli.oya.traininventory.viewmodel.BrandViewModelFactory;
import com.canli.oya.traininventory.viewmodel.CategoryViewModelFactory;
import com.canli.oya.traininventory.viewmodel.ChosenTrainFactory;
import com.canli.oya.traininventory.viewmodel.SearchViewModelFactory;
import com.canli.oya.traininventory.viewmodel.TrainsViewModelFactory;

public class InjectorUtils {

    private static TrainRepository provideTrainRepo(Context context){
        TrainDatabase db = TrainDatabase.getInstance(context);
        return TrainRepository.getInstance(db);
    }

    private static BrandRepository provideBrandRepo(Context context){
        TrainDatabase db = TrainDatabase.getInstance(context);
        AppExecutors executors = AppExecutors.getInstance();
        return BrandRepository.getInstance(db, executors);
    }

    private static CategoryRepository provideCategoryRepo(Context context){
        TrainDatabase db = TrainDatabase.getInstance(context);
        return CategoryRepository.getInstance(db);
    }

    public static ChosenTrainFactory provideChosenTrainFactory(Context context, int trainId){
        TrainDatabase db = TrainDatabase.getInstance(context);
        return new ChosenTrainFactory(db, trainId);
    }

    public static TrainsViewModelFactory provideTrainVMFactory(Context context){
        TrainRepository trainRepo = provideTrainRepo(context);
        return new TrainsViewModelFactory(trainRepo);
    }

    public static BrandViewModelFactory provideBrandVMFactory(Context context){
        BrandRepository brandRepo = provideBrandRepo(context);
        return new BrandViewModelFactory(brandRepo);
    }

    public static CategoryViewModelFactory provideCategoryVMFactory(Context context){
        CategoryRepository categoryRepo = provideCategoryRepo(context);
        return new CategoryViewModelFactory(categoryRepo);
    }

    public static SearchViewModelFactory provideSearchVMFactory(Context context){
        TrainDatabase db = TrainDatabase.getInstance(context);
        return new SearchViewModelFactory(db);
    }

}
