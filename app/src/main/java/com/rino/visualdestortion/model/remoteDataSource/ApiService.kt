package com.rino.visualdestortion.model.remoteDataSource

import android.graphics.Bitmap
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.history.AllHistoryResponse
import com.rino.visualdestortion.model.pojo.home.HomeServicesResponse
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
import com.rino.visualdestortion.model.pojo.resetPassword.RequestOTP
import com.rino.visualdestortion.model.pojo.resetPassword.ResetPasswordRequest
import com.rino.visualdestortion.model.pojo.resetPassword.ResponseOTP
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @POST("api/auth/token")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/auth/refreshToken")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse>

    @POST("api/auth/resetPassword")
    suspend fun requestOTP(@Body requestOTP: RequestOTP): Response<ResponseOTP>

    @POST("api/auth/confirmResetPW")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<ResponseOTP>

    @Multipart
    @POST("api/form/storeForm")
    suspend fun setServiceForm(@Header("Authorization"   ) auth: String
                               ,@Part("serviceTypeId"   ) serviceTypeId: RequestBody
                               ,@Part("sectorName"      ) sectorName: RequestBody
                               ,@Part("municipalityName") municipalityName: RequestBody
                               ,@Part("districtName"    ) districtName: RequestBody
                               ,@Part("streetName"      ) streetName: RequestBody
                               ,@Part("lat"             ) lat: RequestBody
                               ,@Part("lng"             ) lng: RequestBody
                               ,@Part beforeImg: MultipartBody.Part
                               ,@Part afterImg: MultipartBody.Part
                               ,@PartMap WorkersTypesList : HashMap<String, RequestBody>
                               ,@PartMap equipmentList : HashMap<String, RequestBody>
                               ,@Part("mSquare"         ) mSquare: Int?
                               ,@Part("mCube"           ) mCube: Int?
                               ,@Part("numberR"         ) numberR: Int?
                               ,@Part("notes"           ) notes: RequestBody ?
                               ,@Part("Percentage"      ) percentage: RequestBody ?
    ): Response<QRCode>

    @GET("api/form/createFrom")
    suspend fun getServiceForm( @Header("Authorization") auth: String): Response<AddServiceResponse>

    @GET("api/home/HomeApi")
    suspend fun getHomeData( @Header("Authorization") auth: String): Response<HomeServicesResponse>

    @GET("api/History/GetServiceTypeHistory")
    suspend fun getHistoryData( @Header("Authorization") auth: String): Response<AllHistoryResponse>
}