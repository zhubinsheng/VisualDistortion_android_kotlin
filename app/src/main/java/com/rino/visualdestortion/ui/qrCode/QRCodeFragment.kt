package com.rino.visualdestortion.ui.qrCode

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rino.visualdestortion.databinding.FragmentQRCodeBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class QRCodeFragment : Fragment() {
    private lateinit var binding: FragmentQRCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            @SuppressLint("ResourceType")
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQRCodeBinding.inflate(inflater, container, false)
        if (getArguments() != null) {
            // The getPrivacyPolicyLink() method will be created automatically.
            val url = getArguments()?.get("QRCodeURL").toString()
            Log.e("QRCodeURL", url)
            downloadQRCode(url)

        }
        binding.navigateToHome.setOnClickListener {
            navigateToHome()
        }
        return binding.root
    }

    private fun navigateToHome() {
        val action = QRCodeFragmentDirections.actionQRCodeToServices()
        findNavController().navigate(action)
    }

    private fun downloadQRCode(url: String) {

        Picasso.with(requireContext()).load(url)
            .into(binding.qrCodeImg, object : Callback {
                override fun onSuccess() {
                    if (binding.progress != null) {
                        binding.progress.setVisibility(View.GONE)
                    }
                }
                override fun onError() {}
            })
   }

}