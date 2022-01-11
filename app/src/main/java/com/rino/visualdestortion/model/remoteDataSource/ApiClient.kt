package com.rino.visualdestortion.model.remoteDataSource

import com.rino.visualdestortion.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiClient {
    companion object {
        private  var retrofit: Retrofit

        init {
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getApi(): ApiService {
            return retrofit.create(ApiService::class.java)
        }
    }
}