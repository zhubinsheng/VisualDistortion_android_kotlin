package com.rino.visualdestortion.ui.login

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
import com.rino.visualdestortion.databinding.FragmentLoginBinding
import com.rino.visualdestortion.model.pojo.login.LoginRequest
import com.rino.visualdestortion.ui.home.MainActivity


class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginFragmentViewModel
    private lateinit var binding: FragmentLoginBinding
    private var email = ""
    private var pass = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = LoginFragmentViewModel(requireActivity().application)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        loginButtonOnClick()
        resetPassOnClick()
        observeData()
    }

    private fun loginButtonOnClick() {
        binding.loginButton.setOnClickListener {
         //   binding.progress.visibility = View.VISIBLE
            email = binding.editTextEmail.text.toString()
            pass = binding.editTextPassword.text.toString()
            validateData()
        }
    }

    private fun resetPassOnClick() {
        binding.resetPassTxt.setOnClickListener {
            navigateToResetPass()
        }
    }

    private fun navigateToResetPass() {
        val action = LoginFragmentDirections.actionLoginToResetPass()
        findNavController().navigate(action)
    }

    private fun observeData() {
        observeSuccessLogin()
        observeIsPrepared()
        observeLoading()
        observeShowError()

    }

    private fun observeSuccessLogin() {
        viewModel.isLogin.observe(viewLifecycleOwner) {
            if (it) {
             //   binding.progress.visibility = View.GONE
                Toast.makeText(
                    requireActivity(),
                    " Login Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.isTodayPrepared()
             //   navigateToHome()

            } else {
                Toast.makeText(
                    requireActivity(),
                    " Invalid UserName or Password ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun observeIsPrepared() {
        viewModel.isPrepared.observe(viewLifecycleOwner) {
            if (it) {
                   navigateToHome()
            //   navigateToDailyPreparation()
            } else {
                  navigateToDailyPreparation()
              //   navigateToHome()
            }
        }
    }

    private fun navigateToHome() {
        val action = LoginFragmentDirections.actionLoginToServiceFragment()
        findNavController().navigate(action)
    }

    private fun navigateToDailyPreparation() {
        val action = LoginFragmentDirections.actionLoginToDailyPreparation()
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
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(getResources().getColor(R.color.teal))
                    .setActionTextColor(getResources().getColor(R.color.white)).setAction("Ok")
                    {
                    }.show()
            }
        }
    }

    private fun validateData() {
        validateEmail()
        validatPassword()
        if (validateEmail() && validatPassword()) {
            viewModel.login(LoginRequest(email, pass))
        }
    }

    private fun validateEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return if (email.isEmpty()) {
            binding.textInputEmail.error = " برجاء ادخال العنصر"
            false
        } else if (email.length > 50) {
            binding.textInputEmail.error = "البريد الالكترونى يجيب الا يزيد عن 50 حرف "
            false
        } else if (!email.matches(emailPattern)) {
            binding.textInputEmail.error = "بريد الكترونى خاطئ "
            false
        } else {
            binding.textInputEmail.error = null
            binding.textInputEmail.isErrorEnabled = false
            true
        }
    }

    private fun validatPassword(): Boolean {
        val passwordVal = "^" + "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                //   "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{8,}"  //at least 8 characters
//                "$"
        return if (pass.isEmpty()) {
            binding.textInputPassword.error = "برجاء ادخال ها العنصر"
            false
        } else if (!pass.matches(passwordVal.toRegex())) {
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