package com.rino.visualdestortion.model.localDataSource.room

import android.app.Application
import kotlinx.coroutines.flow.Flow

class DailyPreparationDataSource {
    private var dailyPreparationDao: DailyPreparationDao

    constructor(application: Application) {
        dailyPreparationDao = DailyPreparationDB.getDatabase(application).DailyPreparationDao()
    }

     fun getAllData(): Flow<List<DailyPreparation>> {
        return dailyPreparationDao.getAllData()
    }

     fun getDailyPreparation_By_ServiceTypeID(serviceTypeID: String): DailyPreparation{
        return dailyPreparationDao.getDailyPreparation_By_ServiceTypeID(serviceTypeID)
    }

    suspend fun insertDailyPreparation(dailyPreparation: DailyPreparation){
        dailyPreparationDao.insertDailyPreparation(dailyPreparation)
    }

    suspend fun deleteAll(){
        dailyPreparationDao.deleteAll()
    }


}