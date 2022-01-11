package com.rino.visualdestortion.ui.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import com.rino.visualdestortion.R
import com.rino.visualdestortion.ui.MainActivity


class ServicesFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_services, container, false)
    }
    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = false
    }

}