package com.canlioya.local.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Train
import com.canlioya.core.models.TrainMinimal
import com.canlioya.local.TrainDatabase
import com.canlioya.local.entities.toTrain
import com.canlioya.local.entities.toTrainEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

const val TRAINS_PAGE_SIZE = 15

class TrainDataSource(private val database: TrainDatabase) : ITrainDataSource {

    override fun getAllTrains(): Flow<PagingData<TrainMinimal>> {
        val pager = Pager(config = PagingConfig(TRAINS_PAGE_SIZE, enablePlaceholders = true)) {
            database.trainDao().observeAllTrains()
        }
        return pager.flow
    }

    override fun getChosenTrain(trainId: Int) = database.trainDao().observeChosenTrain(trainId).map { it.toTrain() }

    override suspend fun isThisTrainNameUsed(trainName: String): Boolean {
        return database.trainDao().isThisTrainNameUsed(trainName) != null
    }

    override suspend fun insertTrain(train: Train) = database.trainDao().insert(train.toTrainEntity())

    override suspend fun updateTrain(train: Train) = database.trainDao().update(train.toTrainEntity())

    override suspend fun sendTrainToTrash(trainId: Int, dateOfDeletion: Long) = database.trainDao().sendToThrash(trainId, dateOfDeletion)

    override suspend fun deleteTrainPermanently(trainId: Int) {
        database.trainDao().deletePermanently(trainId)
    }

    override suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal> = database.trainDao().getTrainsFromThisBrand(brandName)

    override suspend fun getTrainsFromThisCategory(category: String): List<TrainMinimal> = database.trainDao().getTrainsFromThisCategory(category)

    override suspend fun searchInTrains(
        keyword: String?,
        category: String?,
        brand: String?
    ): ArrayList<TrainMinimal> {

        val filteredList = ArrayList<TrainMinimal>()

        if (!keyword.isNullOrBlank()) {
            val sb = StringBuilder("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE ")
            val keywords = keyword.split(" ")
            val wordCount = keywords.size
            keywords.forEachIndexed { index, keyword ->
                sb.append("(trainName LIKE '%$keyword%' OR modelReference LIKE '%$keyword%' OR description LIKE '%$keyword%') ")
                if (index < wordCount - 1) {
                    sb.append("AND ")
                }
            }

            category?.let {
                sb.append("AND categoryName = '$category' ")
            }

            brand?.let {
                sb.append("AND brandName = '$brand' ")
            }

            // TODO: add an option to search in trash or not
            sb.append("AND dateOfDeletion IS NULL ORDER BY trainName;")

            val query = SimpleSQLiteQuery(sb.toString())
            filteredList.addAll(database.trainDao().searchInTrains(query))
        } else {
            category?.let {
                filteredList.addAll(getTrainsFromThisCategory(it))
                Timber.d("list size: ${filteredList.size}")
            }

            brand?.let { brand ->
                if (filteredList.isNotEmpty()) {
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

    override suspend fun getAllTrainsInTrash() = database.trainDao().observeAllTrainsInTrash()

    override suspend fun restoreTrainFromTrash(trainId: Int) = database.trainDao().restoreFromThrash(trainId)
}
