package com.rino.visualdestortion.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.rino.visualdestortion.databinding.FragmentWelcomeBinding
import com.rino.visualdestortion.ui.home.MainActivity


class WelcomeFragment : Fragment() {
lateinit var binding : FragmentWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        binding.loginBtn.setOnClickListener({
           navigateToLogin()
        })
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = true
    }
    private fun navigateToLogin() {
        val action = WelcomeFragmentDirections.actionWelcomeToLogin()
        findNavController().navigate(action)
    }

}