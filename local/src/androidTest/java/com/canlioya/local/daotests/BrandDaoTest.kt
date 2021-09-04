package com.canlioya.local.daotests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canlioya.local.TrainDatabase
import com.canlioya.local.entities.BrandEntity
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class BrandDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TrainDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
                getApplicationContext(),
                TrainDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertBrand_verifyBrandIsInserted() = runBlockingTest {
        //Insert a brand
        val insertedBrand = BrandEntity(
            brandId = 5,
            brandName = "Marklin",
            brandLogoUri = "brand logo uri",
            webUrl = "https://www.google.com/"
        )
        database.brandDao().insert(insertedBrand)

        //Get back the brand
        val loadedBrand = database.brandDao().getChosenBrand(insertedBrand.brandId)

        //Verify it has expected values
        assertThat(loadedBrand as? BrandEntity, notNullValue())
        assertThat(loadedBrand.brandId, `is`(insertedBrand.brandId))
        assertThat(loadedBrand.brandName, `is`(insertedBrand.brandName))
        assertThat(loadedBrand.brandLogoUri, `is`(insertedBrand.brandLogoUri))
        assertThat(loadedBrand.webUrl, `is`(insertedBrand.webUrl))
    }

    @Test
    fun updateBrand_verifyUpdated() = runBlockingTest {
        //Insert a brand
        val insertedBrand = BrandEntity(
            brandId = 5,
            brandName = "Marklin",
            brandLogoUri = "brand logo uri",
            webUrl = "https://www.google.com/"
        )
        database.brandDao().insert(insertedBrand)

        //Update the brand
        insertedBrand.brandName = "updated name"
        insertedBrand.brandLogoUri = "updated logo url"
        insertedBrand.webUrl = "https://github.com/"
        database.brandDao().update(insertedBrand)

        //Get back the brand
        val loadedBrand = database.brandDao().getChosenBrand(insertedBrand.brandId)

        //Verify it has updated values
        assertThat(loadedBrand as? BrandEntity, notNullValue())
        assertThat(loadedBrand.brandId, `is`(insertedBrand.brandId))
        assertThat(loadedBrand.brandName, `is`(insertedBrand.brandName))
        assertThat(loadedBrand.brandLogoUri, `is`(insertedBrand.brandLogoUri))
        assertThat(loadedBrand.webUrl, `is`(insertedBrand.webUrl))
    }

    @Test
    fun deleteBrand_verifyDeleted() = runBlockingTest {
        //Insert two brands
        val firstBrand = BrandEntity(
            brandId = 5,
            brandName = "Marklin",
            brandLogoUri = "first brand logo uri",
            webUrl = "https://www.google.com/"
        )
        val secondBrand = BrandEntity(
            brandId = 6,
            brandName = "MDN",
            brandLogoUri = "second brand logo uri",
            webUrl = "https://github.com/"
        )
        database.brandDao().insert(firstBrand)
        database.brandDao().insert(secondBrand)

        //Then delete first brand
        database.brandDao().delete(firstBrand)

        //Verify first brand is deleted and second brand is still on database
        val allBrands = database.brandDao().getBrandList()
        assertFalse(allBrands.contains(firstBrand))
        assertTrue(allBrands.contains(secondBrand))
    }
}