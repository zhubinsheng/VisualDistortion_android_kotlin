package com.rino.visualdestortion.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rino.visualdestortion.databinding.FragmentSettingBinding


class settingFragment : Fragment() {
    private lateinit var viewModel: SettingViewModel
    private lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = SettingViewModel(requireActivity().application)
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.logoutTxt.setOnClickListener {
            viewModel.logout()
            navToLogin()
        }
    }

    private fun navToLogin() {
        val action = settingFragmentDirections.actionSettingToLogin()
        findNavController().navigate(action)
    }


}