package com.rino.visualdestortion.model.localDataSource

import android.content.SharedPreferences

class MySharedPreferance class MySharedPreference(private val sharedPreference: SharedPreferences) {

    fun setBoolean (key: String,value: Boolean = false ){
        sharedPreference.edit().putBoolean(key,value)?.apply()
    }

    fun getBoolean(key: String):Boolean{
        return sharedPreference.getBoolean(key,false)?:false
    }
    fun setString (key: String,value: String ){
        sharedPreference.edit().putString(key,value)?.apply()
    }

    fun getString(key: String):String{
        return sharedPreference.getString(key,"")?:""
    }

//    fun setLong (key: String,value: Long = 0 ){
//        sharedPreference.edit().putLong(key,value)?.apply()
//    }
//
//    fun getLong(key: String):Long{
//        return sharedPreference.getLong(key,0)
//    }




}