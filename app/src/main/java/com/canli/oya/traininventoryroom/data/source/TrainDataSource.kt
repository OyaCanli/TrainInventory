package com.canli.oya.traininventoryroom.data.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

const val TRAINS_PAGE_SIZE = 15

class TrainDataSource @Inject constructor(private val database: TrainDatabase) : ITrainDataSource {

    override fun getAllTrains() : Flow<PagingData<TrainMinimal>> {
        val pager = Pager(config = PagingConfig(TRAINS_PAGE_SIZE, enablePlaceholders = true)) {
            database.trainDao().observeAllTrains()
        }
        return pager.flow
    }

    override fun getChosenTrain(trainId : Int) = database.trainDao().observeChosenTrain(trainId)

    override suspend fun getAllTrainNames(): List<String> = database.trainDao().getAllTrainNames()

    override suspend fun insertTrain(train: TrainEntry) = database.trainDao().insert(train)

    override suspend fun updateTrain(train: TrainEntry) = database.trainDao().update(train)

    override suspend fun deleteTrain(trainId: Int, dateOfDeletion : Long) = database.trainDao().sendToThrash(trainId, dateOfDeletion)

    override suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal> = database.trainDao().getTrainsFromThisBrand(brandName)

    override suspend fun getTrainsFromThisCategory(category: String): List<TrainMinimal> = database.trainDao().getTrainsFromThisCategory(category)

    override suspend fun searchInTrains(
        keyword: String?,
        category: String?,
        brand: String?
    ): ArrayList<TrainMinimal> {

        val filteredList = ArrayList<TrainMinimal>()

        if(!keyword.isNullOrBlank()) {
            val sb = StringBuilder("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE ")
            val keywords = keyword.split(" ")
            val wordCount = keywords.size
            keywords.forEachIndexed { index, keyword ->
                sb.append("(trainName LIKE '%$keyword%' OR modelReference LIKE '%$keyword%' OR description LIKE '%$keyword%') ")
                if(index < wordCount - 1){
                    sb.append("AND ")
                }
            }

            category?.let {
                sb.append("AND categoryName = '$category' ")
            }

            brand?.let {
                sb.append("AND brandName = '$brand' ")
            }

            //TODO: add an option to search in trash or not
            sb.append("AND dateOfDeletion IS NULL ORDER BY trainName;")

            val query = SimpleSQLiteQuery(sb.toString())
            filteredList.addAll(database.trainDao().searchInTrains(query))
        } else {
            category?.let {
                filteredList.addAll(getTrainsFromThisCategory(it))
                Timber.d("list size: ${filteredList.size}")
            }

            brand?.let { brand ->
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

    override suspend fun getAllTrainsInTrash() = database.trainDao().getAllTrainsInTrash()

    override suspend fun restoreTrainFromTrash(trainId : Int) = database.trainDao().restoreFromThrash(trainId)
}

