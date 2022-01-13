package com.rino.visualdestortion.model.remoteDataSource



import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
import retrofit2.Response


class ApiDataSource:ApiInterface {
    val retrofit = ApiClient.getApi()
    override suspend fun login(loginRequest: LoginRequest?): Response<LoginResponse> {
          return  retrofit.login(loginRequest!!)

    }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest?): Response<LoginResponse> {
        return retrofit.refreshToken(refreshTokenRequest)
    }

    override suspend fun setServiceForm(auth:String,serviceForm: Map<String, String>): Response<QRCode> {
          return retrofit.setServiceForm(auth,serviceForm)
    }

    override suspend fun getServiceForm(auth:String): Response<AddServiceResponse> {

          return retrofit.getServiceForm(auth)   }

}