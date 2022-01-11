package com.rino.visualdestortion.model.remoteDataSource



import com.rino.visualdestortion.model.pojo.LoginRequest
import com.rino.visualdestortion.model.pojo.LoginResponse
import retrofit2.Response


class ApiDataSource:ApiInterface {
    val retrofit = ApiClient.getApi()
    override suspend fun login(loginRequest: LoginRequest?): Response<LoginResponse> {
        println("@@@@@@login"+loginRequest.toString())
        return  retrofit.login(loginRequest!!)

    }

}