package com.rino.visualdestortion.model.remoteDataSource

import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @POST("api/auth/token")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/auth/refreshToken")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse>

    @POST("api/form/storeForm")
    @FormUrlEncoded
    suspend fun setServiceForm(@Header("Authorization") auth: String,@FieldMap serviceForm: Map<String, String>): Response<QRCode>


    @GET("api/form/createFrom")
    suspend fun getServiceForm( @Header("Authorization") auth: String): Response<AddServiceResponse>


}