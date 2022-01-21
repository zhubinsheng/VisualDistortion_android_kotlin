package com.rino.visualdestortion.model.remoteDataSource



import android.util.Log
import com.google.gson.Gson
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
import retrofit2.Response


class ApiDataSource:ApiInterface {
    private val retrofit = ApiClient.getApi()
    override suspend fun login(loginRequest: LoginRequest?): Response<LoginResponse> {
          return  retrofit.login(loginRequest!!)

    }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse> {
        return retrofit.refreshToken(refreshTokenRequest)
    }

    override suspend fun setServiceForm(auth:String,serviceForm: FormData): Response<QRCode> {

        Log.e("ApiDataSource",serviceForm.beforeImg.toString())
        Log.e("ApiDataSource",serviceForm.afterImg.toString())
          return retrofit.setServiceForm(auth,serviceForm.serviceTypeId,serviceForm.sectorName,
                                        serviceForm.municipalityName,serviceForm.districtName,
                                        serviceForm.streetName,serviceForm.lat,serviceForm.lng,
                                        serviceForm.beforeImg,serviceForm.afterImg,
                                       serviceForm.equipmentList,serviceForm.WorkersTypesList,
                                        serviceForm.mSquare,serviceForm.mCube,serviceForm.numberR,
                                        serviceForm.notes,serviceForm.percentage)



   //     return retrofit.uploadData()
    }

    override suspend fun getServiceForm(auth:String): Response<AddServiceResponse> {

          return retrofit.getServiceForm(auth)   }

}