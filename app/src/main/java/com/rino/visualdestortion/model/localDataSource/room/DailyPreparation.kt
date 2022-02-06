package com.rino.visualdestortion.model.localDataSource.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DailyPreparation")
data class DailyPreparation (@PrimaryKey  @SerializedName("serviceTypeID") val serviceTypeID: String,
                             @SerializedName("date") val date : String,
                             @SerializedName("equipmentList") val equipmentList: Map<Long,Int>,
                             @SerializedName("workerTypesList") val workerTypesList: Map<Long,Int>)


