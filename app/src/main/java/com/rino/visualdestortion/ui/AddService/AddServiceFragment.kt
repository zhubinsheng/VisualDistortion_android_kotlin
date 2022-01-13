package com.rino.visualdestortion.ui.AddService

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.rino.visualdestortion.databinding.FragmentAddServiceBinding



class AddServiceFragment : Fragment() {
    private lateinit var viewModel: AddServiceViewModel
    private lateinit var binding: FragmentAddServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = AddServiceViewModel(requireActivity().application)
        binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel.getServicesData()
        observeData()
    }

    private fun observeData() {
        observeGetServicesData()
        //observeLoading()
    }

    private fun observeGetServicesData() {

            viewModel.getServicesData.observe(viewLifecycleOwner, {
                it.let {
                    Toast.makeText(
                        requireActivity(),
                        " getData Successfully"+it.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                  //  navigateToHome()

                }
            })
        }

//    private fun observeLoading() {
//        viewModel.loading.observe(viewLifecycleOwner, {
//            it?.let {
//                binding.progress.visibility=it
//            }
//        })
//    }


}