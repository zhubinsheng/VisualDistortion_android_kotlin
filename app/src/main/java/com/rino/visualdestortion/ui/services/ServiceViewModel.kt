package com.rino.visualdestortion.ui.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rino.visualdestortion.model.reposatory.ModelRepo

class ServiceViewModel(application: Application) : AndroidViewModel(application) {
    private val modelRepository: ModelRepo = ModelRepo(application)
    private var _navToAddService: MutableLiveData<String> = MutableLiveData()
    val navToAddService: LiveData<String>
        get() = _navToAddService

    fun navToAddService(service: String) {
        _navToAddService.value = service
    }


}