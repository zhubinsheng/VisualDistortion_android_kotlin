package com.rino.visualdestortion.model.pojo.addService

import com.google.gson.annotations.SerializedName

data class AddServiceResponse(
    @SerializedName("sectors"      ) var sectors      : ArrayList<Sectors>?      = arrayListOf(),
//    @SerializedName("equipment"    ) var equipment    : ArrayList<Equipment>    = arrayListOf(),
//    @SerializedName("workerTypes"  ) var workerTypes  : ArrayList<WorkerTypes>  = arrayListOf(),
    @SerializedName("serviceTypes" ) var serviceTypes : ArrayList<ServiceTypes> = arrayListOf()

)
