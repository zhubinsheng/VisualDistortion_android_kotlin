package com.rino.visualdestortion.ui.editDailyPreparation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentEditDailyPreparationBinding
import com.rino.visualdestortion.model.localDataSource.room.DailyPreparation
import com.rino.visualdestortion.model.pojo.dailyPraperation.PrepEquipments
import com.rino.visualdestortion.model.pojo.dailyPraperation.PrepWorkers
import com.rino.visualdestortion.model.pojo.dailyPraperation.TodayDailyPrapration
import com.rino.visualdestortion.ui.home.MainActivity
import java.text.DateFormat
import java.util.*


class EditDailyPreparationFragment : Fragment() {

    private lateinit var viewModel: EditDailyPViewModel
    private lateinit var binding: FragmentEditDailyPreparationBinding
    private lateinit var dailyPreparation: TodayDailyPrapration
    //   private lateinit var addServiceResponse: AddServiceResponse
    private lateinit var equipmentList: ArrayList<String>
    private lateinit var workersTypeList: ArrayList<String>
    private lateinit var equipmentsAdapter: EquipmentsAdapter
    private lateinit var workerTypesAdapter: WorkerTypesAdapter
    private lateinit var equipmentsMap: HashMap<Int, Int>
    private lateinit var workersTypeMap: HashMap<Int, Int>
    private lateinit var equipmentsCountList: ArrayList<PrepEquipments>
    private lateinit var workerTypesCountList: ArrayList<PrepWorkers>
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
        viewModel = EditDailyPViewModel(requireActivity().application)
        binding = FragmentEditDailyPreparationBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setUpUI()
        initLists()
        observeData()
    }

    private fun setUpUI() {
        // binding.serviceTypeNameTxt.text = "${serviceName}التحضير اليومى ل"
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
        viewModel.getTodayPreparation()
        if (getArguments() != null) {

            serviceName = getArguments()?.get("serviceName").toString()
            serviceTypeId = getArguments()?.get("serviceID").toString()
            //      binding.serviceTypeNameTxt.text = serviceName

        }
        binding.saveButton.setOnClickListener {
            if(validateData()) {
                val date = DateFormat.getDateInstance().format(Calendar.getInstance().time).toString()
                viewModel.addDailyPreparation(DailyPreparation(serviceTypeId, date ,equipmentsAdapter.getEquipmentMap(),workerTypesAdapter.getWorkerTypesMap()))
                //   Toast.makeText(requireContext(),"item : ${viewModel.getDailyPreparationByServiceID(serviceTypeId).toString()}",Toast.LENGTH_SHORT).show()
                Log.e("WorkerTypes: ",workerTypesAdapter.getWorkerTypesMap().toString())
                Log.e("Equipment: ",equipmentsAdapter.getEquipmentMap().toString())

                viewModel.editDailyPreparation(workerTypesAdapter.getWorkerTypesMap(),equipmentsAdapter.getEquipmentMap())
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
        // observeGetServicesData()
        observeGetDailyPreparation()
        observeEditDailyPreparation()
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

//    private fun observeGetServicesData() {
//        viewModel.getServicesData.observe(viewLifecycleOwner) {
//            it.let {
//                addServiceResponse = it
//                prepareMenues()
//
//            }
//        }
//    }

    private fun observeGetDailyPreparation() {
        viewModel.getDailyPreparation.observe(viewLifecycleOwner) {
            it.let {
                if (it != null) {
                    dailyPreparation = it
                    equipmentsCountList = it.prepEquipments
                    equipmentsAdapter.updateItems(equipmentsCountList)
                    workerTypesCountList = it.prepWorkers
                    workerTypesAdapter.updateItems(workerTypesCountList)
                    prepareMenues()
                }
     //           prepareMenues()

            }
        }
    }

    private fun observeEditDailyPreparation() {
        viewModel.editDailyPreparation.observe(viewLifecycleOwner) {
            it?.let {
                Log.e("ddddd",it.toString())
                if(it)
                {
                    //  Log.e("ddddd",it.toString())
                    navigateToHome()
                }
            }
        }
    }

    private fun navigateToHome() {
        val action = EditDailyPreparationFragmentDirections.actionEditDailyPreparationToServices()
        findNavController().navigate(action)
    }

    private fun prepareMenues() {
        setEquipmentsMenuItems()
        setWorkersTypeMenuItems()
    }

    private fun setEquipmentsMenuItems() {
        equipmentList.clear()
        var index = 0
        binding.equipmentsTextView.setText(R.string.select)
//        for (equipment in addServiceResponse.equipment!!) {
        for (equipment in dailyPreparation.equipmentTypes) {
            if (!isEquipmentListContainsItem(equipment.id)) {
                equipmentList.add(equipment.name.toString())
            }
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
                    equipmentsMap[position]?.let { PrepEquipments(it, selectedItem, 1) }

                if (item != null) {
                    equipmentsCountList.add(item)
                    equipmentList.remove(item.name)
                }
                //         Toast.makeText(requireContext(),"Selected : ${selectedItem}",
                //             Toast.LENGTH_SHORT).show()
                equipmentsAdapter.updateItems(equipmentsCountList)
            }
    }


    private fun isEquipmentListContainsItem(id:Int): Boolean {
        for (equipment in dailyPreparation.prepEquipments)
        {
            if(id==equipment.id)
            {
                return true
            }
        }
        return false
    }

    private fun setWorkersTypeMenuItems() {
        workersTypeList.clear()
        var index = 0
        binding.workersTypeTextView.setText(R.string.select)
//        for (workerType in addServiceResponse.workerTypes!!) {
        for (workerType in dailyPreparation.workerTypes) {
            if(!isWorkerTypeListContainsItem(workerType.id)) {
                workersTypeList.add(workerType.name.toString())

            }
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
                Log.e("iteeeeem",workersTypeMap[position].toString())
                var item =
                    workersTypeMap[position]?.let { PrepWorkers(it,selectedItem,  1) }
                Log.e("iteeeeem",item.toString())
                if (item != null) {
                    workerTypesCountList.add(item)
                    workersTypeList.remove(item.name)
                    workerTypesAdapter.updateItems(workerTypesCountList)
                    //Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun isWorkerTypeListContainsItem(id: Int): Boolean {
        for (workerType in dailyPreparation.prepWorkers)
        {
            if(id==workerType.id)
            {
                return true
            }
        }
        return false
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun validateData(): Boolean {
        var flagEquipment = true
        var flagworkerType = true
        if (equipmentsCountList.isEmpty()) {
            binding.equipmentsTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flagEquipment = false
        }
        else{
            binding.equipmentsTextInputLayout.error = null
            binding.equipmentsTextInputLayout.isErrorEnabled = false
            flagEquipment = true
        }
        if (workerTypesCountList.isEmpty()) {
            binding.workersTypeTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flagworkerType = false
        }
        else{
            binding.workersTypeTextInputLayout.error = null
            binding.workersTypeTextInputLayout.isErrorEnabled = false
            flagEquipment = true
        }
        return (flagEquipment && flagworkerType)
    }

}


