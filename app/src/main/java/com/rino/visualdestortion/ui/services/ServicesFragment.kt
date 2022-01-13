package com.rino.visualdestortion.ui.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.rino.visualdestortion.databinding.FragmentServicesBinding
import com.rino.visualdestortion.ui.MainActivity



class ServicesFragment : Fragment() {
    private lateinit var viewModel: ServiceViewModel
    private lateinit var binding: FragmentServicesBinding
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var servicesList: List<String>

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
//        binding.shimmer.startShimmerAnimation()
        binding.serviceRecycle.visibility = View.VISIBLE
        serviceAdapter = ServiceAdapter(arrayListOf(), viewModel)
        setUpUI()
        observeData()
        val list = arrayListOf("الباعة الجائلين وأصحاب البسطات المخالفة","المظلات المخالفة","اللوحات الاعلانية المخالفة الكبيرة","الملصقات واللوحات الدعائية الصغيرة","الكتابات المشوهة على الجدران","الأتربة والاحجام الكبيرة من مخلفات الهدم")
        serviceAdapter.updateServices(list)

    }

    private fun observeData() {
        observeNavToAddService()
        observeService()
    }
    private fun observeService() {
//        viewModel.fetchData()
//        viewModel.articles.observe(viewLifecycleOwner, {
//            it?.let {
//                servicesAdapter.updateHome(it)
//                servicesList = it
//                binding.serviceRecycle.visibility = View.VISIBLE
//             //   binding.shimmer.visibility = View.GONE
//            }
//        })
    }

    private fun setUpUI() {
        binding.serviceRecycle.apply {
            layoutManager = GridLayoutManager(requireContext(),2 )
            adapter = serviceAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = false
    }
    private fun observeNavToAddService() {
        viewModel.navToAddService.observe(viewLifecycleOwner, {
            it?.let {
                val action = ServicesFragmentDirections.actionServiceToAddService( )
                findNavController().navigate(action)
            }
        })
    }
}