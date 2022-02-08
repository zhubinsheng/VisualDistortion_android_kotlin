package com.rino.visualdestortion.ui.services

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentServicesBinding
import com.rino.visualdestortion.model.pojo.home.ServiceTypes
import com.rino.visualdestortion.ui.home.MainActivity


class ServicesFragment : Fragment() {
    private lateinit var viewModel: ServiceViewModel
    private lateinit var binding: FragmentServicesBinding
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var servicesList: List<ServiceTypes>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServicesBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel = ServiceViewModel(requireActivity().application)
        serviceAdapter = ServiceAdapter(arrayListOf(), viewModel,requireActivity())
        setUpUI()
        observeData()
        serviceAdapter.updateServices(emptyList())

    }

    private fun observeData() {
        observeNavToAddService()
        observeNavToDailyPreparation()
        observeService()
        observeLoading()
        observeShowError()
    }

    private fun observeService() {
        viewModel.getServicesData()
        viewModel.getServicesData.observe(viewLifecycleOwner) {
            it?.let {
                serviceAdapter.updateServices(it.serviceTypes)
                servicesList = it.serviceTypes
                binding.serviceRecycle.visibility = View.VISIBLE

            }
        }
    }

    private fun setUpUI() {
        binding.serviceRecycle.visibility = View.VISIBLE
        binding.serviceRecycle.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = serviceAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = false
    }

    private fun observeNavToAddService() {
        viewModel.navToAddService.observe(viewLifecycleOwner) {
            it?.let {
                val action = ServicesFragmentDirections.actionServiceToAddService(
                    it.name,
                    it.id.toString())
                findNavController().navigate(action)
            }
        }
    }
    private fun observeNavToDailyPreparation() {
        viewModel.navToDailyPreparation.observe(viewLifecycleOwner) {
            it?.let {
                val action = ServicesFragmentDirections.actionServiceToDailyprepration(
                    it.name,
                    it.id.toString())
                findNavController().navigate(action)
            }
        }
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.progress.visibility = it
            }
        }
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                        resources.getColor(
                            R.color.teal
                        )
                    )
                    .setActionTextColor(resources.getColor(R.color.white)).setAction("Ok")
                    {
                    }.show()
            }
        }
    }
}