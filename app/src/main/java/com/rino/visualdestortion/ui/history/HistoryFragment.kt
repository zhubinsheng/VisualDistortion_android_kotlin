package com.rino.visualdestortion.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentHistoryBinding
import com.rino.visualdestortion.model.pojo.history.Data
import com.rino.visualdestortion.ui.home.MainActivity


class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyList: List<Data>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        viewModel = HistoryViewModel(requireActivity().application)
        historyAdapter = HistoryAdapter(arrayListOf(), viewModel)
        setUpUI()
        observeData()
        historyAdapter.updateItems(emptyList())
    }

    private fun observeData() {
        observeHistoryData()
        observeLoading()
        observeShowError()
    }

    private fun observeHistoryData() {
        viewModel.getHistoryData()
        viewModel.getHistoryData.observe(viewLifecycleOwner) {
            it?.let {
                it.data?.let { it1 -> historyAdapter.updateItems(it1)
                    historyList = it1 }

                binding.historyRecycle.visibility = View.VISIBLE

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