package com.rino.visualdestortion.ui.resetPassward

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rino.visualdestortion.model.reposatory.ModelRepo

class ResetPasswordViewModel (application: Application) : AndroidViewModel(application) {
    private val modelRepository: ModelRepo = ModelRepo(application)

}