package com.rino.visualdestortion.ui.historyByServiceType

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentHistoryByServiceTypeBinding
import com.rino.visualdestortion.model.pojo.history.HistoryByServiceIdResponse
import com.rino.visualdestortion.model.pojo.history.ServiceData
import com.rino.visualdestortion.ui.dailyPreparation.EquipmentItem
import com.rino.visualdestortion.ui.home.MainActivity


class HistoryByServiceTypeFragment : Fragment() {
    private lateinit var viewModel: HistoryByServiceViewModel
    private lateinit var binding: FragmentHistoryByServiceTypeBinding
    private lateinit var historyAdapter: HistoryByServiceAdapter
    private lateinit var historyList: ArrayList<ServiceData>
    private lateinit var periodTimeList_ar: ArrayList<String>
    private lateinit var periodTimeList_en: ArrayList<String>
    private lateinit var historyByServiceIdResponse: HistoryByServiceIdResponse
    private var selectedPeriod = "all"
    private var serviceId = 1
    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceId = arguments?.get("serviceID").toString().toInt()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryByServiceTypeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        periodTimeList_ar = arrayListOf(" الكل "," الاسبوع الحالى "," الاسبوع السابق "," الشهر الحالى "," الشهر السابق "," السنة الحالية "," السنة السابقة "," منذ عامين ")
        periodTimeList_en = arrayListOf("all","week","lastweek","month","lastmonth","year","lastyear","twoyearsago")
        binding.shimmer.startShimmer()
        binding.historyRecycle.visibility = View.GONE
        viewModel = HistoryByServiceViewModel(requireActivity().application)
        historyList = arrayListOf()
        historyAdapter = HistoryByServiceAdapter(historyList, viewModel)
        historyAdapter.updateItems(historyList)
        setUpUI()
        observeData()
        historyAdapter.updateItems(arrayListOf())
    }

    private fun observeData() {
        observeHistoryData()
        observeLoading()
        observeShowError()
    }


    private fun observeHistoryData() {
        viewModel.getHistoryData(serviceId)
        viewModel.getHistoryData.observe(viewLifecycleOwner) {
            it?.let {
                historyByServiceIdResponse = it
                Log.e("hasNextPage",it.hasNextPage.toString())
                Log.e("totalPages",it.totalPages.toString())
                it.data?.let { it1 ->
                    historyAdapter.updateItems(it1)
                    historyList = it1
                }
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
        (activity as MainActivity).bottomNavigation.isGone = true
    }

    private fun setUpUI() {
        binding.historyRecycle.visibility = View.VISIBLE
        binding.historyRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
        setPeriodTimeMenuItems()
        setupPagination()

    }

    private fun setupPagination() {
        binding.idNestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.
                Log.e("historyByServiceIdResponse", historyByServiceIdResponse.toString())
                if (historyByServiceIdResponse.hasNextPage == true) {
                    page++
                    Log.e("pageNermeen", page.toString())
                    Log.e("hasNext", historyByServiceIdResponse.hasNextPage.toString())
                    viewModel.viewLoading(View.VISIBLE)
                    viewModel.getHistoryData(serviceId, page ,selectedPeriod)
                } else {
                    viewModel.viewLoading(View.GONE)
                }
            }
        })
    }

    private fun setPeriodTimeMenuItems() {

        binding.periodTimeTextView.setText(R.string.period_time)

        val adapter = ArrayAdapter(
            requireContext(), R.layout.dropdown_item,
            periodTimeList_ar
        )
        binding.periodTimeTextView.setAdapter(adapter)
        binding.periodTimeTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                historyAdapter.clearList()
                page = 1
                val index = periodTimeList_ar.indexOf(selectedItem)
                selectedPeriod = periodTimeList_en[index]
              viewModel.getHistoryData(serviceId,page,periodTimeList_en[index])

            }

    }

}