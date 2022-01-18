package com.rino.visualdestortion.model.pojo.addService

import android.graphics.Bitmap
import retrofit2.http.Field

data class FormData(var serviceTypeId: String
,var sectorName: String
,var municipalityName: String
,var districtName: String
,var streetName: String
,var lat: String
,var lng: String
,var beforeImg: Bitmap
,var afterImg: Bitmap
,var WorkersTypesList: Map<Long,Int>
,var equipmentList: Map<Long,Int>
,var mSquare: Int?
,var mCube: Int?
,var numberR: Int?
,var notes: String?)
