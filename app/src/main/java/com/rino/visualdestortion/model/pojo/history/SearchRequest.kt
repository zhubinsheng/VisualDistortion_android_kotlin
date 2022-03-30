package com.rino.visualdestortion.model.pojo.history

import com.google.gson.annotations.SerializedName

data class SearchRequest(
    @SerializedName("id"          ) var id          : Int? = null,
    @SerializedName("serviceType" ) var serviceType : Int? = null
)
