package com.rino.visualdestortion.ui.fingerPrint

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rino.visualdestortion.model.reposatory.ModelRepo

class FingerPrintViewModel (application: Application) : AndroidViewModel(application) {
    private val modelRepository: ModelRepo = ModelRepo(application)
    fun logout(){
        modelRepository.logout()
    }
    fun isLogin():Boolean{
       return modelRepository.isLogin()
    }
}