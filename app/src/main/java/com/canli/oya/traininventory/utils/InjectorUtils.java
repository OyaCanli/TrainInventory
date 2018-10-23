package com.canli.oya.traininventory.utils;

import android.content.Context;

import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.repositories.BrandRepository;
import com.canli.oya.traininventory.data.repositories.CategoryRepository;
import com.canli.oya.traininventory.data.repositories.TrainRepository;
import com.canli.oya.traininventory.viewmodel.ChosenTrainFactory;

public class InjectorUtils {

    public static TrainRepository provideTrainRepo(Context context){
        TrainDatabase db = TrainDatabase.getInstance(context);
        AppExecutors executors = AppExecutors.getInstance();
        return TrainRepository.getInstance(db, executors);
    }

    public static BrandRepository provideBrandRepo(Context context){
        TrainDatabase db = TrainDatabase.getInstance(context);
        AppExecutors executors = AppExecutors.getInstance();
        return BrandRepository.getInstance(db, executors);
    }

    public static CategoryRepository provideCategoryRepo(Context context){
        TrainDatabase db = TrainDatabase.getInstance(context);
        return CategoryRepository.getInstance(db);
    }

    public static ChosenTrainFactory provideChosenTrainFactory(Context context, int trainId){
        TrainDatabase db = TrainDatabase.getInstance(context);
        return new ChosenTrainFactory(db, trainId);
    }

}
