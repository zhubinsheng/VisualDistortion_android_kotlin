package com.rino.visualdestortion.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rino.visualdestortion.model.reposatory.ModelRepo

class SplashViewModel (application: Application) : AndroidViewModel(application) {
    private val modelRepository: ModelRepo = ModelRepo(application)
    fun isLogin():Boolean{
      return modelRepository.isLogin()
    }
}