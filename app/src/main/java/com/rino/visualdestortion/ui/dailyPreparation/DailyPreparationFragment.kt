package com.rino.visualdestortion.ui.dailyPreparation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentDailyPreparationBinding
import com.rino.visualdestortion.model.localDataSource.room.DailyPreparation
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.ui.home.MainActivity
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DailyPreparationFragment : Fragment() {
    private lateinit var viewModel: DailyPreparationViewModel
    private lateinit var binding: FragmentDailyPreparationBinding
    private lateinit var addServiceResponse: AddServiceResponse
    private lateinit var equipmentList: ArrayList<String>
    private lateinit var workersTypeList: ArrayList<String>
    private lateinit var equipmentsAdapter: EquipmentsAdapter
    private lateinit var workerTypesAdapter: WorkerTypesAdapter
    private lateinit var equipmentsMap: HashMap<Int?, Int?>
    private lateinit var workersTypeMap: HashMap<Int?, Int?>
    private lateinit var equipmentsCountList: ArrayList<EquipmentItem>
    private lateinit var workerTypesCountList: ArrayList<EquipmentItem>
    private lateinit var equipmentsCountMap: HashMap<Long?, Int?>
    private lateinit var workerTypesCountMap: HashMap<Long?, Int?>
    private var serviceTypeId = ""
    private var serviceName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceName = getArguments()?.get("serviceName").toString()
            serviceTypeId = getArguments()?.get("serviceID").toString()
        }
    }
    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = DailyPreparationViewModel(requireActivity().application)
        binding = FragmentDailyPreparationBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setUpUI()
        initLists()
        observeData()
    }

    private fun setUpUI() {
        binding.serviceTypeNameTxt.text = R.string.DailyPreparation.toString()+"ل"+serviceName
        equipmentsAdapter = EquipmentsAdapter(arrayListOf(), viewModel, requireContext())
        workerTypesAdapter = WorkerTypesAdapter(arrayListOf(), viewModel, requireContext())
        binding.equipmentsRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = equipmentsAdapter
        }
        binding.workersTypeRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = workerTypesAdapter
        }
        viewModel.getServicesData()
        if (getArguments() != null) {

            serviceName = getArguments()?.get("serviceName").toString()
            serviceTypeId = getArguments()?.get("serviceID").toString()
            binding.serviceTypeNameTxt.text = serviceName
        }
        binding.nextButton.setOnClickListener {
            if(validateData()) {
                val date = DateFormat.getDateInstance().format(Calendar.getInstance().time).toString()
                viewModel.addDailyPreparation(DailyPreparation(serviceTypeId, date ,equipmentsAdapter.getEquipmentMap(),workerTypesAdapter.getWorkerTypesMap()))
             //   Toast.makeText(requireContext(),"item : ${viewModel.getDailyPreparationByServiceID(serviceTypeId).toString()}",Toast.LENGTH_SHORT).show()
                navigateToAddService(serviceName, serviceTypeId)
            }
        }
    }

    private fun initLists() {
        equipmentList = ArrayList()
        workersTypeList = ArrayList()
        equipmentsMap = HashMap()
        workersTypeMap = HashMap()
        equipmentsCountList = ArrayList()
        workerTypesCountList = ArrayList()
        equipmentsCountMap = HashMap()
        workerTypesCountMap = HashMap()
    }

    private fun observeData() {
        observeGetServicesData()
        observeLoading()
        observeShowError()
        observeDeletedEquipmentItem()
        observeDeletedWorkerTypeItem()
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
                        getResources().getColor(
                            R.color.teal
                        )
                    )
                    .setActionTextColor(getResources().getColor(R.color.white)).setAction("Ok")
                    {
                    }.show()
            }
        }
    }

    private fun observeDeletedWorkerTypeItem() {
        viewModel.workerTypeDeleteItem.observe(viewLifecycleOwner) {
            it?.let {
                workerTypesCountList.remove(it)
                workersTypeList.add(it.name)
            }
        }
    }

    private fun observeDeletedEquipmentItem() {
        viewModel.equipmentsDeleteItem.observe(viewLifecycleOwner) {
            it?.let {
                equipmentsCountList.remove(it)
                equipmentList.add(it.name)
            }
        }
    }

    private fun observeGetServicesData() {
        viewModel.getServicesData.observe(viewLifecycleOwner) {
            it.let {
                addServiceResponse = it
                prepareMenues()

            }
        }
    }

    private fun prepareMenues() {
            setEquipmentsMenuItems()
            setWorkersTypeMenuItems()
    }
    private fun setEquipmentsMenuItems() {
        equipmentList.clear()
        var index = 0
        binding.equipmentsTextView.setText(R.string.select)
        for (equipment in addServiceResponse.equipment!!) {
            equipmentList.add(equipment.name.toString())
            equipmentsMap[index] = equipment.id
            index++
        }
        val adapter = ArrayAdapter(
            requireContext(), R.layout.dropdown_item,
            equipmentList
        )
        binding.equipmentsTextView.setAdapter(adapter)
        binding.equipmentsTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                var item =
                    equipmentsMap[position]?.toLong()?.let { EquipmentItem(selectedItem, it, 1) }
                if (item != null) {
                    equipmentsCountList.add(item)
                    equipmentList.remove(item.name)
                }
                //    Toast.makeText(requireContext(),"Selected : ${equipmentsCountList.(item)}",Toast.LENGTH_SHORT).show()
                equipmentsAdapter.updateItems(equipmentsCountList)
            }

    }

    private fun setWorkersTypeMenuItems() {
        workersTypeList.clear()
        var index = 0
        binding.workersTypeTextView.setText(R.string.select)
        for (workerType in addServiceResponse.workerTypes!!) {
            workersTypeList.add(workerType.name.toString())
            workersTypeMap[index] = workerType.id
            index++
        }
        val adapter = ArrayAdapter(
            requireContext(), R.layout.dropdown_item,
            workersTypeList
        )
        binding.workersTypeTextView.setAdapter(adapter)
        binding.workersTypeTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                var item =
                    workersTypeMap[position]?.toLong()?.let { EquipmentItem(selectedItem, it, 1) }
                if (item != null) {
                    workerTypesCountList.add(item)
                    workersTypeList.remove(item.name)
                    workerTypesAdapter.updateItems(workerTypesCountList)
                    //Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
                }

            }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun validateData(): Boolean {
        var flag = true
        if (equipmentList.isEmpty()) {
            binding.equipmentsTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flag = false
        }
        if (workersTypeList.isEmpty()) {
            binding.workersTypeTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flag = false
        }

        return flag
    }

    private fun navigateToAddService(serviceTypeName: String,serviceTypeID: String) {
        val action = DailyPreparationFragmentDirections.actionDailyPreparationToAddService(serviceTypeName,serviceTypeID)
        findNavController().navigate(action)
    }

}