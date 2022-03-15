package com.rino.visualdestortion.ui.qrCode

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rino.visualdestortion.databinding.FragmentQRCodeBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class QRCodeFragment : Fragment() {
    private lateinit var binding: FragmentQRCodeBinding
    private lateinit var qrCodeImg:Bitmap
    private var url = ""
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
             url = getArguments()?.get("QRCodeURL").toString()
            Log.e("QRCodeURL", url)
            downloadQRCode(url)

        }
        binding.navigateToHome.setOnClickListener {
            navigateToHome()
        }
        binding.shareInWhatsapp.setOnClickListener{
            shareQRCodeInWhatsapp()
        }
        return binding.root
    }

    private fun shareQRCodeInWhatsapp() {
        val bitmap = ( binding.qrCodeImg.drawable.toBitmap())
        val imgUri: Uri = getImageUri(bitmap)
        var whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, url)
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri)
        whatsappIntent.type = "image/*"
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            requireActivity()!!.startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
//            Toast.makeText(
//                context, " الوتساب غير مثبت  ",
//                Toast.LENGTH_SHORT
//            ).show()
            showWarningDialog(" الوتساب غير مثبت  ")

        }
    }
    private fun showWarningDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setNegativeButton(" الغاء ",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            .setCancelable(true)
            .create().show()
    }
    fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            inImage,
            "QRCode_IMG_" + currentDate.toString().replace(" ",""),
            null
        )
//        val path = MediaStore.Images.Media.insertImage(
//            inContext.contentResolver,
//            inImage,
//            "After_IMG_",
//            null
//        )
        return Uri.parse(path)
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