package com.rino.visualdestortion.model.reposatory

import android.content.Context
import android.util.Log
import com.rino.visualdestortion.model.localDataSource.MySharedPreference
import com.rino.visualdestortion.model.localDataSource.Preference
import com.rino.visualdestortion.model.localDataSource.PreferenceDataSource
import com.rino.visualdestortion.model.pojo.LoginRequest
import com.rino.visualdestortion.model.pojo.LoginResponse
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