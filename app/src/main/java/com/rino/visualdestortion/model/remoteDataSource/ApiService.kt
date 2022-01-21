package com.rino.visualdestortion.model.remoteDataSource

import android.graphics.Bitmap
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @POST("api/auth/token")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/auth/refreshToken")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<LoginResponse>

    @Multipart
    @POST("api/form/storeForm")
    suspend fun setServiceForm(@Header("Authorization"   ) auth: String
                               ,@Part("serviceTypeId"   ) serviceTypeId: String
                               ,@Part("sectorName"      ) sectorName: String
                               ,@Part("municipalityName") municipalityName: String
                               ,@Part("districtName"    ) districtName: String
                               ,@Part("streetName"      ) streetName: String
                               ,@Part("lat"             ) lat: String
                               ,@Part("lng"             ) lng: String
                               ,@Part beforeImg: MultipartBody.Part
                               ,@Part afterImg: MultipartBody.Part
                               ,@Part("WorkersTypesList") WorkersTypesList : Map<Long,Int>
                               ,@Part("EquipmentList"   ) equipmentList :  Map<Long,Int>
                               ,@Part("mSquare"         ) mSquare: Int?
                               ,@Part("mCube"           ) mCube: Int?
                               ,@Part("numberR"         ) numberR: Int?
                               ,@Part("notes"           ) notes: String?
                               ,@Part("Percentage"      ) percentage: String?
    ): Response<QRCode>

    @Multipart
    @POST("api/form/storeForm")
    suspend fun uploadData(@PartMap map: HashMap<String?, FormData?>): Response<QRCode>

    @GET("api/form/createFrom")
    suspend fun getServiceForm( @Header("Authorization") auth: String): Response<AddServiceResponse>


}