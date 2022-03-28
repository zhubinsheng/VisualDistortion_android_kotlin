package com.rino.visualdestortion.ui.qrCodeResult

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentQrCodeResultBinding
import com.rino.visualdestortion.databinding.FragmentServiceDetailsBinding
import com.rino.visualdestortion.model.pojo.history.ServiceData
import com.rino.visualdestortion.ui.serviceDetails.ServiceDetailsFragmentDirections
import com.rino.visualdestortion.ui.serviceDetails.SliderAdapter
import com.rino.visualdestortion.ui.serviceDetails.SliderData
import com.smarteist.autoimageslider.SliderView

class QrCodeResultFragment : Fragment() {
    private lateinit var sliderDataArrayList: ArrayList<SliderData>
    private lateinit var sliderAdapter : SliderAdapter
    private lateinit var binding: FragmentQrCodeResultBinding
    private  lateinit var service: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQrCodeResultBinding.inflate(inflater, container, false)
        arguments?.let {
            service = arguments?.get("scanResult").toString()
            var result = service.lines()
            Log.e("scan result",result.size.toString())
            setUpUI(result)

        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        binding.imageSlider.startAutoCycle()
    }
    private fun setUpUI(result: List<String>) {


        val createdBy    =  result[0].split(":")
        val createdDate  =  result[1].split(":")
        val serviceName  =  result[2].split(":")
        val beforeImgUrl =  result[3].split(":")
        val afterImgUrl  =  result[4].split(":")
        val locationUrl  =  result[5].split(":")

        binding.menetorNameValue.text = createdBy[1]
        binding.addressValue.text = createdDate[1]
        binding.serviceNumValue.text = serviceName[1]

        binding.viewLocationBtn.setOnClickListener{
            navToLocationInMap(locationUrl[1])
        }

        sliderDataArrayList= arrayListOf(SliderData(beforeImgUrl[1],"الصورة قبل المهمة"),
            SliderData(afterImgUrl[1],"الصورة بعد المهمة"))
        binding.imageSlider.autoCycleDirection = SliderView.LAYOUT_DIRECTION_RTL
        sliderAdapter = SliderAdapter(requireContext(),sliderDataArrayList)
        binding.imageSlider.setSliderAdapter(sliderAdapter)
        binding.imageSlider.scrollTimeInSec = 3
        binding.imageSlider.isAutoCycle = true
        binding.imageSlider.setCurrentPageListener {
            val currentPos = binding.imageSlider.currentPagePosition
            when(currentPos){
                0->binding.titleTxt.text ="الصورة قبل المهمة"
                1->binding.titleTxt.text ="الصورة بعد المهمة"
            }
        }
    }

    private fun navToLocationInMap(locationUrl: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
        startActivity(browserIntent)
    }
}