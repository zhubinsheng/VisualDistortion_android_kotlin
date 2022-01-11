package com.rino.visualdestortion.model.remoteDataSource

import com.rino.visualdestortion.model.pojo.LoginRequest
import com.rino.visualdestortion.model.pojo.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/token")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

}