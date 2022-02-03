package com.rino.visualdestortion.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.rino.visualdestortion.R
import com.rino.visualdestortion.ui.home.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        splashSetup()
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
    private fun splashSetup(){
        CoroutineScope(Dispatchers.Default).launch{
            delay(3000)
            CoroutineScope(Dispatchers.Main).launch{
                findNavController().popBackStack()
                findNavController().navigate(R.id.welcomeFragment)
            }
        }
    }

}