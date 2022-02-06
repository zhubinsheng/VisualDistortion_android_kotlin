package com.rino.visualdestortion.model.localDataSource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPreparationDao {

    @Query("SELECT * FROM DailyPreparation")
    fun getAllData(): Flow<List<DailyPreparation>>

    @Query("SELECT * FROM DailyPreparation WHERE serviceTypeID=:serviceTypeID ")
    fun getDailyPreparation_By_ServiceTypeID(serviceTypeID: String): DailyPreparation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyPreparation(dailyPreparation: DailyPreparation)

    @Query("DELETE FROM DailyPreparation")
    suspend fun deleteAll()


}