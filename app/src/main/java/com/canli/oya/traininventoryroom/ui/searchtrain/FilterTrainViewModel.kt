package com.canli.oya.traininventoryroom.ui.searchtrain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.lang.StringBuilder


class FilterTrainViewModel (val trainDataSource: ITrainDataSource,
                            val brandDataSource: IBrandCategoryDataSource<BrandEntry>,
                            val categoryDataSource : IBrandCategoryDataSource<CategoryEntry>,
                            private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var selectedBrand : MutableLiveData<String?> = MutableLiveData(null)

    var selectedCategory : MutableLiveData<String?> = MutableLiveData(null)

    var keyword : String? = null

    suspend fun getBrandNames() = brandDataSource.getItemNames()

    suspend fun getCategoryNames() = categoryDataSource.getItemNames()

    suspend fun getTrainsFromThisBrand(brandName: String) = trainDataSource.getTrainsFromThisBrand(brandName)

    suspend fun getTrainsFromThisCategory(category: String) = trainDataSource.getTrainsFromThisCategory(category)

    suspend fun filterTrains() : ArrayList<TrainMinimal> {
        val filteredList = ArrayList<TrainMinimal>()

        Timber.d("filtered trains is called")

        if(!keyword.isNullOrBlank()) {
            val sb = StringBuilder("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE ")
            val keywords = keyword!!.split(" ")
            val wordCount = keywords.size
            keywords.forEachIndexed { index, keyword ->
                sb.append("(trainName LIKE '%$keyword%' OR modelReference LIKE '%$keyword%' OR description LIKE '%$keyword%') ")
                if(index < wordCount - 1){
                    sb.append("AND ")
                }
            }

            if(selectedCategory.value != null) {
                sb.append("AND categoryName = '${selectedCategory.value}' ")
            }

            if(selectedBrand.value != null) {
                sb.append("AND brandName = '${selectedBrand.value}' ")
            }

            sb.append(";")

            val query = SimpleSQLiteQuery(sb.toString())
            filteredList.addAll(trainDataSource.searchInTrains(query))
        } else {
            selectedCategory.value?.let {
                filteredList.addAll(getTrainsFromThisCategory(it))
                Timber.d("list size: ${filteredList.size}")
            }

            if(selectedCategory.value == null) {
                Timber.d("selected category is null")
            }

            selectedBrand.value?.let { brand ->
                if(filteredList.isNotEmpty()){
                    val filtered = filteredList.filter { train ->
                        train.brandName == brand
                    }
                    Timber.d("list size: ${filtered.size}")
                    filteredList.clear()
                    filteredList.addAll(filtered)
                } else {
                    filteredList.addAll(getTrainsFromThisBrand(brand))
                }
            }
        }

        return filteredList
    }




}