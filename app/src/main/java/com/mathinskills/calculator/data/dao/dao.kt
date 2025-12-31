package com.mathinskills.calculator.data.dao

import androidx.room.*
import com.mathinskills.calculator.data.entity.CalculationEntity
import com.mathinskills.calculator.data.entity.RegionStandardEntity

@Dao
interface CalculationDao {

    @Query("SELECT * FROM calculation_table ORDER BY id DESC")
    suspend fun getAll(): List<CalculationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CalculationEntity)

    @Query("DELETE FROM calculation_table WHERE id = :id")
    suspend fun deleteById(id: Int)
}


@Dao
interface RegionStandardDao {

    @Query("SELECT * FROM region_standard_table WHERE regionCode = :regionCode")
    suspend fun getByRegion(regionCode: String): List<RegionStandardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RegionStandardEntity>)
}