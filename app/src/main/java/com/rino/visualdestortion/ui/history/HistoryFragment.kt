package com.rino.visualdestortion.ui.history

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentHistoryBinding
import com.rino.visualdestortion.model.pojo.history.Data
import com.rino.visualdestortion.ui.AddService.AddServiceFragmentDirections
import com.rino.visualdestortion.ui.home.MainActivity


class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyList: List<Data>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            @SuppressLint("ResourceType")
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.shimmer.startShimmer()
        binding.historyRecycle.visibility = View.GONE
        viewModel = HistoryViewModel(requireActivity().application)
        historyAdapter = HistoryAdapter(arrayListOf(), viewModel)
        setUpUI()
        observeData()
        historyAdapter.updateItems(emptyList())
    }

    private fun observeData() {
        observeHistoryData()
        observeNavToService()
        observeLoading()
        observeShowError()
    }

    private fun observeHistoryData() {
        viewModel.getHistoryData()
        viewModel.getHistoryData.observe(viewLifecycleOwner) {
            it?.let {
                it.data?.let { it1 -> historyAdapter.updateItems(it1)
                    historyList = it1 }
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE
                binding.historyRecycle.visibility = View.VISIBLE
                binding.animationView.visibility = View.GONE

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

    private fun observeNavToService() {
        viewModel.navToHistoryOfTask.observe(viewLifecycleOwner) {
            it?.let {
               navToHistoryById(it)
            }
        }
    }

    private fun navToHistoryById(id: Int) {
        val action = HistoryFragmentDirections.actionHistoryToHistoryByID(id.toString())
        findNavController().navigate(action)
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
//                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
//                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
//                        resources.getColor(
//                            R.color.teal
//                        )
//                    )
//                    .setActionTextColor(resources.getColor(R.color.white)).setAction("Ok")
//                    {
//                    }.show()
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE
                binding.historyRecycle.visibility = View.GONE
                binding.animationView.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = false
    }

    private fun setUpUI() {
        binding.historyRecycle.visibility = View.VISIBLE
        binding.historyRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

}