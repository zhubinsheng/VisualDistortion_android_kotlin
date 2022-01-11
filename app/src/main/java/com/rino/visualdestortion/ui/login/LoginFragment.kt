package com.rino.visualdestortion.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.databinding.FragmentLoginBinding
import com.rino.visualdestortion.model.pojo.LoginRequest


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginButtonOnClick()
    }

    private fun loginButtonOnClick() {
        binding.loginButton.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            email = binding.editTextEmail.text.toString()
            pass = binding.editTextPassword.text.toString()
            validateData()
        }
    }

    private fun init() {
        observeData()

    }

    private fun observeData() {
        observeSuccessLogin()
        observeLoading()
        observeShowError()

    }
    private fun observeSuccessLogin() {
        viewModel.isLogin.observe(viewLifecycleOwner, {
            if (it) {
                binding.progress.visibility = View.GONE
                Toast.makeText(
                    requireActivity(),
                    " Login Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToHome()

            } else {
                Toast.makeText(
                    requireActivity(),
                    " Invalid UserName or Password ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun navigateToHome() {
        val action = LoginFragmentDirections.actionLoginToHomeFragment()
        findNavController().navigate(action)
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner, {
            it?.let {
           binding.progress.visibility=it
            }
        })
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAction("Ok") {
                    }.show()
            }
        })
    }
    private fun validateData() {
        validateEmail()
        validatPassword()
        if (validateEmail() && validatPassword()) {
            viewModel.login(LoginRequest(email,pass))
        }
    }
    private fun validateEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return if (email.isEmpty()) {
            binding.textInputEmail.error = "Field cannot be empty"
            false
        }else if(email.length>50) {
            binding.textInputEmail.error = "Email Must not be more than 50 characters"
            false
        }else if (!email.matches(emailPattern)) {
            binding.textInputEmail.error = "Invalid email address"
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
        return if (pass.isEmpty()) {
            binding.textInputPassword.error = "Field cannot be empty"
            false
        } else if (!pass.matches(passwordVal.toRegex())) {
            binding.textInputPassword.error = "Password is too weak"
            false
        } else {
            binding.textInputPassword.error = null
            binding.textInputPassword.isErrorEnabled = false
            true
        }
    }
}