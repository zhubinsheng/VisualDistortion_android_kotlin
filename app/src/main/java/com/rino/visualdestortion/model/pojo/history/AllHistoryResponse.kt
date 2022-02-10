package com.rino.visualdestortion.model.pojo.history

import com.google.gson.annotations.SerializedName

data class AllHistoryResponse (
    @SerializedName("data" )     var data : ArrayList<Data>? = arrayListOf(),
    @SerializedName("dateTime" ) var dateTime : String?
)