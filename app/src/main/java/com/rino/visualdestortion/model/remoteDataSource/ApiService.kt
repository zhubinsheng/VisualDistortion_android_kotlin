package com.rino.visualdestortion.model.remoteDataSource

import android.graphics.Bitmap
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
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

    @FormUrlEncoded
    @POST("api/form/storeForm")
    suspend fun setServiceForm(@Header("Authorization"   ) auth: String
                               ,@Field("serviceTypeId"   ) serviceTypeId: String
                               ,@Field("sectorName"      ) sectorName: String
                               ,@Field("municipalityName") municipalityName: String
                               ,@Field("districtName"    ) districtName: String
                               ,@Field("streetName"      ) streetName: String
                               ,@Field("lat"             ) lat: String
                               ,@Field("lng"             ) lng: String
                               ,@Field("beforeIm"        ) beforeImg: Bitmap
                               ,@Field("afterImg"        ) afterImg: Bitmap
                               ,@FieldMap WorkersTypesList: Map<String,Map<Long?,Int?>>
                               ,@FieldMap equipmentList: Map<String,Map<Long?,Int?>>
                               ,@Field("mSquare"         ) mSquare: Int?
                               ,@Field("mCube"           ) mCube: Int?
                               ,@Field("numberR"         ) numberR: Int?
                               ,@Field("notes"           ) notes: String?
                               ,@Field("Percentage"      ) percentage: String?

    ): Response<QRCode>

    @Multipart
    @POST("api/form/storeForm")
    suspend fun uploadData(@PartMap map: HashMap<String?, FormData?>): Response<QRCode>

    @GET("api/form/createFrom")
    suspend fun getServiceForm( @Header("Authorization") auth: String): Response<AddServiceResponse>


}