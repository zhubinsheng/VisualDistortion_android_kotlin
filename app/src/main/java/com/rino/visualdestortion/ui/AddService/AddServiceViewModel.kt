package com.rino.visualdestortion.ui.AddService

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.model.pojo.addService.QRCode
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.model.remoteDataSource.Result
import com.rino.visualdestortion.model.reposatory.ModelRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddServiceViewModel(application: Application) : AndroidViewModel(application) {
    private val modelRepository: ModelRepo = ModelRepo(application)

    private var _setError = MutableLiveData<String>()
    private var _loading = MutableLiveData<Int>(View.GONE)
    private var _navigateToQRCode = MutableLiveData<String>()
    private var _getServicesData = MutableLiveData<AddServiceResponse>()
    private var _setServiceForm = MutableLiveData<QRCode>()
    private var _equipmentsDeleteItem = MutableLiveData<EquipmentItem>()
    private var _workerTypeDeleteItem = MutableLiveData<EquipmentItem>()

    val loading: LiveData<Int>
        get() = _loading

    val setError: LiveData<String>
        get() = _setError


    val getServicesData: LiveData<AddServiceResponse>
        get() = _getServicesData

    val setServiceForm: LiveData<QRCode>
        get() = _setServiceForm

    val equipmentsDeleteItem: LiveData<EquipmentItem>
        get() = _equipmentsDeleteItem

    val workerTypeDeleteItem: LiveData<EquipmentItem>
        get() = _workerTypeDeleteItem


    fun getServicesData() {

        viewModelScope.launch(Dispatchers.IO) {
            when (val result =  modelRepository.getServiceForm() ) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    Log.i("getServiceData:", "${result.data}")
                    _getServicesData.postValue(result.data!!)
                    _loading.postValue(View.GONE)

                }
                is Result.Error -> {
                    Log.e("getServiceData:", "${result.exception.message}")
                    _setError.postValue(result.exception.message)
                    _loading.postValue(View.GONE)

                }
                is Result.Loading -> {
                    Log.i("getServiceData", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }

    }

    fun setFormData(serviceForm: FormData) {

        viewModelScope.launch(Dispatchers.IO) {
            when (val result =  modelRepository.setServiceForm(serviceForm) ) {
                is Result.Success -> {
                    _loading.postValue(View.GONE)
                    Log.i("setServiceForm:", "${result.data}")
                    _setServiceForm.postValue(result.data!!)
                    _loading.postValue(View.GONE)

                }
                is Result.Error -> {
                    Log.e("setServiceForm:", "${result.exception.message}")
                    _setError.postValue(result.exception.message)
                    _loading.postValue(View.GONE)

                }
                is Result.Loading -> {
                    Log.i("setServiceForm", "Loading")
                    _loading.postValue(View.VISIBLE)
                }
            }
        }

    }
   fun setEquipmentDeletedItem(equipmentItem: EquipmentItem){
       _equipmentsDeleteItem.value=equipmentItem
   }
    fun setWorkerTypeDeletedItem(workerTypeItem: EquipmentItem){
       _workerTypeDeleteItem.value = workerTypeItem
    }
    fun isFirstTimeLaunch():Boolean{
     return   modelRepository.getFirstTimeLaunch()
    }
    fun setFirstTimeLaunch(firstTimeLaunch:Boolean){
           modelRepository.setFirstTimeLaunch(firstTimeLaunch)
    }
}