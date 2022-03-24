package com.rino.visualdestortion.model.remoteDataSource

import android.util.Log
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.dailyPraperation.CheckDailyPreparationResponse
import com.rino.visualdestortion.model.pojo.dailyPraperation.GetDailyPraprationData
import com.rino.visualdestortion.model.pojo.dailyPraperation.TodayDailyPrapration
import com.rino.visualdestortion.model.pojo.history.AllHistoryResponse
import com.rino.visualdestortion.model.pojo.history.HistoryByServiceIdResponse
import com.rino.visualdestortion.model.pojo.history.SearchResponse
import com.rino.visualdestortion.model.pojo.history.ServiceData
import com.rino.visualdestortion.model.pojo.home.HomeServicesResponse
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
import com.rino.visualdestortion.model.pojo.resetPassword.RequestOTP
import com.rino.visualdestortion.model.pojo.resetPassword.ResetPasswordRequest
import com.rino.visualdestortion.model.pojo.resetPassword.ResponseOTP
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response



class ApiDataSource:ApiInterface {
    private val retrofit = ApiClient.getApi()
    override suspend fun login(loginRequest: LoginRequest?): Response<LoginResponse> {
          return  retrofit.login(loginRequest!!)

    }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse> {
        return retrofit.refreshToken(refreshTokenRequest)
    }

    override suspend fun requestOTP(requestOTP: RequestOTP): Response<ResponseOTP> {
        return retrofit.requestOTP(requestOTP)
    }

    override suspend fun resetPassword(resetPasswrdRequest: ResetPasswordRequest): Response<ResponseOTP> {
        return retrofit.resetPassword(resetPasswrdRequest)
    }


    override suspend fun setServiceForm(auth:String, serviceForm: FormData): Response<QRCode?>? {
        Log.e("ApiDataSource",serviceForm.beforeImg.toString())
        Log.e("ApiDataSource",serviceForm.afterImg.toString())
        val serviceTypeIdBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.serviceTypeId)
        val sectorNameBody:RequestBody    = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.sectorName)
        val municipalityNameBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.municipalityName)
        val districtNameBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.districtName)
        val streetNameBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.streetName)
        val latBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.lat)
        val lngBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.lng)
        val notesBody: RequestBody? = serviceForm.notes?.let {
            RequestBody.create("text/plain".toMediaTypeOrNull(),
                it
            )
        }
    //    val percentageBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.percentage)

//        val workerTypesList: HashMap<String, RequestBody> = HashMap()
//        for (item in serviceForm.WorkersTypesList)
//        {
//            workerTypesList["WorkersTypesList[${item.key}]"] = item.value.toString().toRequestBody()
//        }
//
//        val equipmentList: HashMap<String, RequestBody> = HashMap()
//        for (item in serviceForm.equipmentList)
//        {
//
//            workerTypesList["EquipmentList[${item.key}]"] = item.value.toString().toRequestBody()
//        }

        return retrofit.setServiceForm(auth,serviceTypeIdBody,sectorNameBody,
                municipalityNameBody,districtNameBody,
                streetNameBody,latBody,lngBody,
                serviceForm.beforeImg, serviceForm.duringImg,serviceForm.afterImg,
                serviceForm.mSquare,serviceForm.mCube,serviceForm.numberR,
                notesBody)

   //     return retrofit.uploadData()
    }

    override suspend fun getServiceForm(auth:String): Response<AddServiceResponse> {

          return retrofit.getServiceForm(auth)   }

    override suspend fun getHomeData(auth: String): Response<HomeServicesResponse> {

        return retrofit.getHomeData(auth)
    }

    override suspend fun getHistoryData(auth: String): Response<AllHistoryResponse> {
        return retrofit.getHistoryData(auth)
    }

    override suspend fun getHistoryDataByService(
        auth: String,
        serviceTypeId: Int,
        pageNumber: Int,
        period:String
    ): Response<HistoryByServiceIdResponse> {
        return retrofit.getHistoryDataByService(auth,serviceTypeId,pageNumber,period)
    }

    override suspend fun searchHistoryDataByService(
        auth: String,
        taskNumber: String
    ): Response<SearchResponse> {
       return retrofit.searchHistoryDataByService(auth,taskNumber)
    }

    override suspend fun isDailyPrepared(auth: String): Response<CheckDailyPreparationResponse> {
       return  retrofit.isDailyPrepared(auth)
    }

    override suspend fun getCreateDailyPreparation(auth: String): Response<GetDailyPraprationData> {
        return retrofit.getCreateDailyPreparation(auth)
    }

    override suspend fun setDailyPreparation(
        auth: String,
        WorkersTypesMap: Map<Long,Int>,
        equipmentMap:    Map<Long, Int>
    ): Response<Void> {
        val workerTypesList: HashMap<String, RequestBody> = HashMap()
        for (item in WorkersTypesMap)
        {
            workerTypesList["WorkersTypesList[${item.key}]"] = item.value.toString().toRequestBody()
        }

        val equipmentList: HashMap<String, RequestBody> = HashMap()
        for (item in equipmentMap)
        {

            workerTypesList["EquipmentList[${item.key}]"] = item.value.toString().toRequestBody()
        }
        return  retrofit.setDailyPreparation(auth,workerTypesList,equipmentList)
    }

    override suspend fun getDailyPreparation(auth: String): Response<TodayDailyPrapration> {
        return  retrofit.getDailyPreparation(auth)
    }
    override suspend fun editDailyPreparation(
        auth: String,
        WorkersTypesMap: Map<Long,Int>,
        equipmentMap:    Map<Long, Int>
    ): Response<Void> {
        val workerTypesList: HashMap<String, RequestBody> = HashMap()
        for (item in WorkersTypesMap)
        {
            workerTypesList["WorkersTypesList[${item.key}]"] = item.value.toString().toRequestBody()
        }

        val equipmentList: HashMap<String, RequestBody> = HashMap()
        for (item in equipmentMap)
        {

            workerTypesList["EquipmentList[${item.key}]"] = item.value.toString().toRequestBody()
        }
        return  retrofit.editDailyPreparation(auth,workerTypesList,equipmentList)
    }
}