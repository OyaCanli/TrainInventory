package com.canli.oya.traininventoryroom.ui.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.entities.BrandEntity
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


class FilterTrainViewModel (val trainDataSource: ITrainDataSource,
                            val brandDataSource: IBrandCategoryDataSource<BrandEntity>,
                            val categoryDataSource : IBrandCategoryDataSource<CategoryEntity>,
                            private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var selectedBrand : MutableLiveData<String?> = MutableLiveData(null)

    var selectedCategory : MutableLiveData<String?> = MutableLiveData(null)

    var keyword : String? = null

    suspend fun getBrandNames() = brandDataSource.getItemNames()

    suspend fun getCategoryNames() = categoryDataSource.getItemNames()

    suspend fun filterTrains() : ArrayList<TrainMinimal> {
        val filteredList = ArrayList<TrainMinimal>()
        filteredList.addAll(trainDataSource.searchInTrains(keyword, selectedCategory.value, selectedBrand.value))
        return filteredList
    }
}