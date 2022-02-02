package com.rino.visualdestortion.ui.resetPassward

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.ui.home.MainActivity
import com.rino.visualdestortion.databinding.FragmentResetPasswordBinding

class ResetPasswordFragment : Fragment() {
    private lateinit var viewModel: ResetPasswordViewModel
    private lateinit var binding: FragmentResetPasswordBinding
    private var email = ""
    private var newPass = ""
    private var otp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ResetPasswordViewModel(requireActivity().application)
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }
    private fun init() {
        requestOTPButtonOnClick()
        saveNewPassOnClick()
        observeData()
    }

    private fun requestOTPButtonOnClick() {

        binding.reuestOtpButton.setOnClickListener {
            email = binding.editTextEmail.text.toString()
            if (validateEmail()) {
                viewModel.requestOTP(email)
            }
        }

    }

    private fun saveNewPassOnClick() {
        binding.saveButton.setOnClickListener {
            getDataFromUI()
            validateData()
        }

    }

    private fun getDataFromUI() {
        email = binding.editTextEmail.text.toString()
        newPass = binding.editTextPassword.text.toString()
        otp = binding.editTextOTPCode.text.toString()
    }

    private fun observeData() {
        observeSuccessOTP()
        observeSuccessResetPass()
        observeLoading()
        observeShowError()
    }

    private fun observeSuccessResetPass() {
        viewModel.resetPass.observe(viewLifecycleOwner) {
            it.let {
                Toast.makeText(
                    requireActivity(),
                    it?.status,
                    Toast.LENGTH_SHORT
                ).show()
                navigateToHome()

            }
        }
    }

    private fun observeSuccessOTP() {
        viewModel.getOTP.observe(viewLifecycleOwner) {
            it.let {
                binding.otpCodeTextInput.visibility = View.VISIBLE
                binding.textInputPassword.visibility = View.VISIBLE
                binding.saveButton.visibility = View.VISIBLE
                binding.reuestOtpButton.visibility = View.GONE
                Toast.makeText(
                    requireActivity(),
                    it?.status,
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun navigateToHome() {
        val action = ResetPasswordFragmentDirections.actionResetPassToServices()
        findNavController().navigate(action)
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.progress.visibility = it
            }
        }
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                        resources.getColor(
                            R.color.teal
                        )
                    )
                    .setActionTextColor(resources.getColor(R.color.white)).setAction("Ok")
                    {
                    }.show()
            }
        }
    }

    private fun validateData() {
        validateEmail()
        validatPassword()
        validateOTP()
        if (validateEmail() && validatPassword() && validateOTP()) {
            viewModel.resetPass(email,otp,newPass)
        }
    }

    private fun validateOTP(): Boolean {
      if(otp.isEmpty()){
          binding.otpCodeTextInput.error = "برجاء ادخال هذا العنصر"
          return false
      }
        if(otp.length!=4){
            binding.otpCodeTextInput.error = "الكود التفعيلى يجب ان يحتوى على 4 ارقام"
            return false
        }
        return true
    }

    private fun validateEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return if (email.isEmpty()) {
            binding.textInputEmail.error = " برجاء ادخال العنصر"
            false
        }else if(email.length>50) {
            binding.textInputEmail.error = "البريد الالكترونى يجب الا يزيد عن 50 حرف "
            false
        }else if (!email.matches(emailPattern)) {
            binding.textInputEmail.error = "بريد الكترونى خاطئ "
            false
        } else {
            binding.textInputEmail.error = null
            binding.textInputEmail.isErrorEnabled = false
            true
        }
    }

    private fun validatPassword(): Boolean {
        val passwordVal = "^" +  "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                //   "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{8,}"  //at least 8 characters
//                "$"
        return if (newPass.isEmpty()) {
            binding.textInputPassword.error = "برجاء ادخال ها العنصر"
            false
        } else if (!newPass.matches(passwordVal.toRegex())) {
            binding.textInputPassword.error = "كلمة المرور ضعيفة"
            false
        } else {
            binding.textInputPassword.error = null
            binding.textInputPassword.isErrorEnabled = false
            true
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = true
    }


}