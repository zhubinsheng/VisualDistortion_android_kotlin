package com.rino.visualdestortion.model.reposatory

import android.content.Context
import android.util.Log
import com.rino.visualdestortion.model.localDataSource.MySharedPreference
import com.rino.visualdestortion.model.localDataSource.Preference
import com.rino.visualdestortion.model.localDataSource.PreferenceDataSource
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.pojo.login.LoginResponse
import com.rino.visualdestortion.model.pojo.login.RefreshTokenRequest
import com.rino.visualdestortion.model.remoteDataSource.ApiDataSource
import com.rino.visualdestortion.model.remoteDataSource.ApiInterface
import com.rino.visualdestortion.model.remoteDataSource.Result
import com.rino.visualdestortion.utils.PREF_FILE_NAME
import java.io.IOException

class ModelRepo (context: Context):RemoteRepo,LocalRepo{
    private val apiDataSource: ApiInterface = ApiDataSource()
    private val preference =
        MySharedPreference(context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE))
    private val sharedPreference: Preference = PreferenceDataSource(preference)


    override suspend fun login(loginRequest: LoginRequest?): Result<LoginResponse?> {
        var result: Result<LoginResponse?> = Result.Loading
        try {
            val response = apiDataSource.login(loginRequest)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
            } else {
                Log.i("ModelRepository", "Error${response.errorBody()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Email or Password invalid"))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                    }
                }
            }

        }catch (e: IOException){
            result = Result.Error(e)
            Log.e("ModelRepository","IOException ${e.message}")
            Log.e("ModelRepository","IOException ${e.localizedMessage}")

        }
        return result
    }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest?): Result<LoginResponse?> {
        var result: Result<LoginResponse?> = Result.Loading

        try {
            val response = apiDataSource.refreshToken(refreshTokenRequest!!)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
                setToken(response.body()?.token!!)
                setRefreshToken(response.body()?.refreshToken!!)
            } else {
                Log.i("ModelRepository", "Error${response.message()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        Log.i("ModelRepository refresh token:", "Result $result")
                        result = Result.Error(Exception("Login Required"))


                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    401 -> {
                        Log.e("Error 401", "Not Auth")
                        result = Result.Error(Exception("Not Auth"))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                    }
                }
            }

        }catch (e: IOException){
            result = Result.Error(e)
            Log.e("ModelRepository","IOException ${e.message}")
            Log.e("ModelRepository","IOException ${e.localizedMessage}")

        }
        return result
    }

    override suspend fun setServiceForm(serviceForm: FormData):Result<QRCode?> {
        var result: Result<QRCode?> = Result.Loading
        try {

            val response = apiDataSource.setServiceForm("Bearer "+getToken(),serviceForm)
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
            } else {
                Log.i("ModelRepository", "Error${response.errorBody()}")
            }

        }catch (e: IOException){
            result = Result.Error(e)
            Log.e("ModelRepository","IOException ${e.message}")
            Log.e("ModelRepository","IOException ${e.localizedMessage}")

        }
        return result
    }

    override suspend fun getServiceForm(): Result<AddServiceResponse?> {
        var result: Result<AddServiceResponse?> = Result.Loading
        try {
            Log.i("ModelRepository:@@", "Token ${getToken()}")

            val response = apiDataSource.getServiceForm("Bearer "+getToken())
            if (response.isSuccessful) {
                result = Result.Success(response.body())
                Log.i("ModelRepository", "Result $result")
            } else {
                Log.i("ModelRepository", "Error${response.message().toString()}")
                when (response.code()) {
                    400 -> {
                        Log.e("Error 400", "Bad Request")
                        result = Result.Error(Exception("Bad Request getData"))
                    }
                    404 -> {
                        Log.e("Error 404", "Not Found")
                    }
                    500 -> {
                        Log.e("Error 500", "Server Error")
                        result = Result.Error(Exception("server is down"))
                    }
                    401 -> {
                        Log.e("Error 401", "Not Auth")
                        if(isLogin())
                            Log.i("Model Repo:", "isLogin:"+isLogin()+", token:"+getToken()+",  refresh token:"+getRefreshToken())
                        //  refreshToken(RefreshTokenRequest(getToken(),getRefreshToken()))
                         //   refreshToken(RefreshTokenRequest("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJheW1hbm9tYXJhNTVAZ21haWwuY29tIiwianRpIjoiNGZlMDQ5NjQtZDRjNC00ZWQ3LTkwOTAtNDhhZWJlMjBhYzJhIiwiZW1haWwiOiJheW1hbm9tYXJhNTVAZ21haWwuY29tIiwiaXNzIjoiaHR0cHM6Ly9hbWFuYXQtamVkZGFoLXN0YWdpbmcuYXp1cmV3ZWJzaXRlcy5uZXQiLCJhdWQiOiJodHRwczovL2FtYW5hdC1qZWRkYWgtc3RhZ2luZy5henVyZXdlYnNpdGVzLm5ldCIsInVpZCI6IjQ1ZmVjYzlkLTI1NjAtNGNlMC04YTY4LTZlMjcyYzM1MDQ2ZiIsIm5iZiI6MTY0MjUwMTA1OCwiZXhwIjoxNjQyNTIyNjU4LCJpYXQiOjE2NDI1MDEwNTh9.MLjmtA69E__oy4aBAcicmMUcSmScYkyD6nK57c4oXCE","l1FAdwyASSqQxJVvAclqv5JkmHgoXWacweK5/iL0L/8="))
                    }
                    else -> {
                        Log.e("Error", "Generic Error")
                    }
                }
            }

        }catch (e: IOException){
            result = Result.Error(e)
            Log.e("ModelRepository","IOException ${e.message}")
            Log.e("ModelRepository","IOException ${e.localizedMessage}")

        }
        return result    }

    override fun isLogin(): Boolean {
        return sharedPreference.isLogin()
    }

    override fun setLogin(login: Boolean) {
        sharedPreference.setLogin(login)
    }

    override fun setEmail(email: String) {
        sharedPreference.setEmail(email)
    }

    override fun getEmail(): String {
        return sharedPreference.getEmail()
    }

    override fun setPass(pass: String) {
        sharedPreference.setPass(pass)
    }

    override fun getPass(): String {
        return sharedPreference.getPass()
    }

    override fun setToken(token: String) {
        sharedPreference.setToken(token)
    }

    override fun getToken(): String {
        return sharedPreference.getToken()
    }

    override fun setRefreshToken(refreshToken: String) {
        sharedPreference.setRefreshToken(refreshToken)
    }

    override fun getRefreshToken(): String {
        return sharedPreference.getRefreshToken()
    }


}