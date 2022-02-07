package com.rino.visualdestortion.model.reposatory

import com.rino.visualdestortion.model.localDataSource.room.DailyPreparation
import kotlinx.coroutines.flow.Flow

interface LocalRepo {
    fun getAllData(): Flow<List<DailyPreparation>>
    fun getDailyPreparation_By_ServiceTypeID(serviceTypeID: String,date :String): DailyPreparation?
    fun insertDailyPreparation(dailyPreparation: DailyPreparation)
    fun deleteAll()
    fun delete_By_ServiceTypeID(serviceTypeID: String,date :String)
    fun getFirstTimeLaunch():Boolean
    fun setFirstTimeLaunch(firstTimeLaunch:Boolean)
    fun isLogin():Boolean
    fun setLogin(login:Boolean)
    fun setEmail(email:String)
    fun getEmail():String
    fun setPass(pass:String)
    fun getPass():String
    fun setToken(token:String)
    fun getToken():String
    fun setRefreshToken(refreshToken:String)
    fun getRefreshToken():String
}