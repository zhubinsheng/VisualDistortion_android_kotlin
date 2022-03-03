package com.rino.visualdestortion.model.pojo.history

import com.google.gson.annotations.SerializedName

data class HistoryByServiceIdResponse(
    @SerializedName("index"           ) var index           : Int?            = null,
    @SerializedName("totalPages"      ) var totalPages      : Int?            = null,
    @SerializedName("hasPreviousPage" ) var hasPreviousPage : Boolean?        = null,
    @SerializedName("hasNextPage"     ) var hasNextPage     : Boolean?        = null,
    @SerializedName("data"            ) var data            : ArrayList<ServiceData> = arrayListOf()
)

data class EquipmentList (
    @SerializedName("name"  ) var name  : String? = null,
    @SerializedName("count" ) var count : Int?    = null
)

data class WorkerstList (
    @SerializedName("title" ) var title : String? = null,
    @SerializedName("count" ) var count : Int?    = null
)


data class ServiceData (

    @SerializedName("id"            ) var id            : String?                  = null,
    @SerializedName("serviceNumber" ) var serviceNumber : Int?                     = null,
    @SerializedName("notes"         ) var notes         : String?                  = null,
    @SerializedName("createdDate"   ) var createdDate   : String?                  = null,
    @SerializedName("beforeImg"     ) var beforeImg     : String?                  = null,
    @SerializedName("duringImg"     ) var duringImg     : String?                  = null,
    @SerializedName("afterImg"      ) var afterImg      : String?                  = null,
    @SerializedName("fullLocation"  ) var fullLocation  : String?                  = null,
    @SerializedName("qrCodeImg"     ) var qrCodeImg     : String?                  = null,
    @SerializedName("sqrd"          ) var sqrd          : String?                  = null,
    @SerializedName("replyCount"    ) var replyCount    : String?                  = null,
    @SerializedName("quantityCubed" ) var quantityCubed : String?                  = null,
    @SerializedName("equipmentList" ) var equipmentList : ArrayList<EquipmentList> = arrayListOf(),
    @SerializedName("workerstList"  ) var workerstList  : ArrayList<WorkerstList>  = arrayListOf()

)