package com.rino.visualdestortion.model.remoteDataSource

import com.rino.visualdestortion.model.pojo.LoginRequest
import com.rino.visualdestortion.model.pojo.LoginResponse
import retrofit2.Response

interface ApiInterface{

    suspend fun login(loginRequest: LoginRequest?): Response<LoginResponse>

}