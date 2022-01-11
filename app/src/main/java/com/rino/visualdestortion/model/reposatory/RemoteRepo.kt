package com.rino.visualdestortion.model.reposatory

import com.rino.visualdestortion.model.pojo.LoginRequest
import com.rino.visualdestortion.model.pojo.LoginResponse
import com.rino.visualdestortion.model.remoteDataSource.Result

interface RemoteRepo {
    suspend fun login(loginRequest: LoginRequest?): Result<LoginResponse?>
}