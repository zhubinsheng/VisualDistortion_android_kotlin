package com.rino.visualdestortion.model.remoteDataSource

import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface{

    suspend fun login(loginRequest: LoginRequest?): Response<LoginResponse>

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest?): Response<LoginResponse>

    suspend fun setServiceForm(auth:String,serviceForm: Map<String, String>): Response<QRCode>

    suspend fun getServiceForm(auth:String): Response<AddServiceResponse>
}
