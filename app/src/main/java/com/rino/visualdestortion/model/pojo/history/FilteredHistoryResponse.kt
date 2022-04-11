package com.rino.visualdestortion.model.pojo.history

class FilteredHistoryResponse {

     var data : ArrayList<Data> = arrayListOf()
}

data class Data (

    var title  : String?          = null,
    var period : String?          = null,
    var count  : Int?             = null,
    var items  : ArrayList<Items> = arrayListOf()
)
data class Items (

     var id            : String?                  = null,
     var serviceNumber : Int?                     = null,
     var notes         : String?                  = null,
     var createdDate   : String?                  = null,
     var beforeImg     : String?                  = null,
     var duringImg     : String?                  = null,
     var afterImg      : String?                  = null,
     var longtitude    : String?                  = null,
     var latitude      : String?                  = null,
     var fullLocation  : String?                  = null,
     var qrCodeImg     : String?                  = null,
     var percentage    : Int?                     = null,
     var sqrd          : String?                  = null,
     var replyCount    : Int?                     = null,
     var quantityCubed : Double?                  = null,
     var equipmentList : ArrayList<EquipmentList> = arrayListOf(),
     var workerstList  : ArrayList<WorkerstList>  = arrayListOf()

)


