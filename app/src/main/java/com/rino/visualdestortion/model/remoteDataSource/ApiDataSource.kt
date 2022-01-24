package com.rino.visualdestortion.model.remoteDataSource





import android.util.Log
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.home.HomeServicesResponse
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
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


    override suspend fun setServiceForm(auth:String, serviceForm: FormData): Response<QRCode> {

        Log.e("ApiDataSource",serviceForm.beforeImg.toString())
        Log.e("ApiDataSource",serviceForm.afterImg.toString())
        val serviceTypeIdBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.serviceTypeId)
        val sectorNameBody:RequestBody    = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.sectorName)
        val municipalityNameBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.municipalityName)
        val districtNameBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.districtName)
        val streetNameBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.streetName)
        val latBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.lat)
        val lngBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.lng)
        val notesBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),
            serviceForm.notes!!
        )
        val percentageBody:RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), serviceForm.percentage)

        val workerTypesList: HashMap<String, RequestBody> = HashMap()
        for (item in serviceForm.WorkersTypesList)
        {
            workerTypesList["WorkersTypesList[${item.key}]"] = item.value.toString().toRequestBody()
        }

        val equipmentList: HashMap<String, RequestBody> = HashMap()
        for (item in serviceForm.equipmentList)
        {

            workerTypesList["EquipmentList[${item.key}]"] = item.value.toString().toRequestBody()
        }

        return retrofit.setServiceForm(auth,serviceTypeIdBody,sectorNameBody,
                                       municipalityNameBody,districtNameBody,
                                       streetNameBody,latBody,lngBody,
                                        serviceForm.beforeImg,serviceForm.afterImg,
                                         equipmentList,workerTypesList,
                                        serviceForm.mSquare,serviceForm.mCube,serviceForm.numberR,
                                        notesBody,percentageBody)



   //     return retrofit.uploadData()
    }

    override suspend fun getServiceForm(auth:String): Response<AddServiceResponse> {

          return retrofit.getServiceForm(auth)   }

    override suspend fun getHomeData(auth: String): Response<HomeServicesResponse> {

        return retrofit.getHomeData(auth)
    }

}