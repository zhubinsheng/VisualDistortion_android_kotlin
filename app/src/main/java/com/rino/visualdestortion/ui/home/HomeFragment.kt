package com.rino.visualdestortion.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentHomeBinding
import com.rino.visualdestortion.databinding.FragmentLoginBinding
import com.rino.visualdestortion.ui.MainActivity
import com.rino.visualdestortion.ui.login.LoginFragmentViewModel


class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = HomeViewModel(requireActivity().application)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = false
    }




}