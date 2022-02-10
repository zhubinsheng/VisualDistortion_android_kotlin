package com.rino.visualdestortion.model.pojo.history

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("serviceName"   ) var serviceName   : String? = null,
                @SerializedName("numberOfTasks" ) var numberOfTasks : Int?    = null,
                @SerializedName("dateFrom"      ) var dateFrom      : String? = null,
                @SerializedName("dateTo"        ) var dateTo        : String? = null,
                @SerializedName("numberOfDays"  ) var numberOfDays  : Int?    = null)
