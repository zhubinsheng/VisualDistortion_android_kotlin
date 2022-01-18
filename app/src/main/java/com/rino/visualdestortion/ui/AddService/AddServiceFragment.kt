package com.rino.visualdestortion.ui.AddService

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentAddServiceBinding
import com.rino.visualdestortion.model.pojo.addService.*
import com.rino.visualdestortion.ui.MainActivity


class AddServiceFragment : Fragment() {
    private lateinit var viewModel: AddServiceViewModel
    private lateinit var binding: FragmentAddServiceBinding
    private lateinit var addServiceResponse:AddServiceResponse
    private lateinit var sectorsList: ArrayList<String>
    private lateinit var municipalitesList: ArrayList<String>
    private lateinit var districtsList: ArrayList<String>
    private lateinit var streetList: ArrayList<String>
    private lateinit var equipmentList: ArrayList<String>
    private lateinit var workersTypeList: ArrayList<String>


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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = true
    }

    private fun init() {
        initLists()
        viewModel.getServicesData()
        observeData()
    }

    private fun initLists() {
        sectorsList = ArrayList()
        municipalitesList = ArrayList()
        districtsList = ArrayList()
        streetList = ArrayList()
        equipmentList = ArrayList()
        workersTypeList = ArrayList()
    }

    private fun observeData() {
        observeGetServicesData()
        observeLoading()
        observeShowError()
    }

    private fun observeGetServicesData() {
            viewModel.getServicesData.observe(viewLifecycleOwner, {
                it.let {
                    addServiceResponse = it
                    Toast.makeText(
                        requireActivity(),
                        " getData Successfully"+it.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    prepareMenues()

                }
            })
        }

    private fun prepareMenues() {
        setSectorsMenuItems()
        setEquipmentsMenuItems()
        setWorkersTypeMenuItems()
    }

    private fun setSectorsMenuItems() {
          sectorsList.clear()
          binding.sectorTextView.setText(R.string.sector)
        for(sector in addServiceResponse.sectors!!){
            sectorsList.add(sector.name.toString())
        }
        val sectorsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
            sectorsList)
        binding.sectorTextView.setAdapter(sectorsAdapter)
        binding.sectorTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            setMunicipalitesMenuItems(position)
            // Display the clicked item using toast
            Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }

    }

    private fun setMunicipalitesMenuItems(posSector: Int) {
        binding.municipalitesTextView.setText(R.string.municipalites)
        municipalitesList.clear()
        for(municipalite in addServiceResponse.sectors?.get(posSector)?.municipalites!!){
            municipalitesList.add(municipalite.name.toString())
        }
        val municipalitesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
            municipalitesList)
        binding.municipalitesTextView.setAdapter(municipalitesAdapter)
        binding.municipalitesTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            setDistrictsMenuItems(posSector,position)
            // Display the clicked item using toast
            Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDistrictsMenuItems(posSector: Int, posMunicipalite: Int) {
      districtsList.clear()
        binding.districtsTextView.setText(R.string.districts)
        for(district in addServiceResponse.sectors?.get(posSector)?.municipalites?.get(posMunicipalite)?.districts!!){
            districtsList.add(district.name.toString())
        }
        val districtsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
            districtsList)
        binding.districtsTextView.setAdapter(districtsAdapter)
        binding.districtsTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            setStreetsMenuItems(posSector,posMunicipalite,position)
            // Display the clicked item using toast
            Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setStreetsMenuItems(posSector: Int, posMunicipalite: Int, posDistricts: Int) {
        streetList.clear()
        binding.streetTextView.clearListSelection()

        for(street in addServiceResponse.sectors?.get(posSector)?.municipalites?.get(posMunicipalite)?.districts?.get(posDistricts)?.streets!!){
            streetList.add(street.name.toString())
        }
        val streetsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
            streetList)
        binding.streetTextView.setAdapter(streetsAdapter)
        binding.streetTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
             //  setStreetsMenuItems(posSector,posMunicipalite,position)
            // Display the clicked item using toast
            Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }
    }
    private fun setEquipmentsMenuItems() {
        equipmentList.clear()
        binding.equipmentsTextView.setText(R.string.select)
        for(equipment in addServiceResponse.equipment!!){
            equipmentList.add(equipment.name.toString())
        }
        val sectorsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
            equipmentList)
        binding.equipmentsTextView.setAdapter(sectorsAdapter)
        binding.equipmentsTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Display the clicked item using toast
            Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }

    }

    private fun setWorkersTypeMenuItems() {
        workersTypeList.clear()
        binding.workersTypeTextView.setText(R.string.select)
        for(workerType in addServiceResponse.workerTypes!!){
            workersTypeList.add(workerType.name.toString())
        }
        val workerTypesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
            workersTypeList)
        binding.workersTypeTextView.setAdapter(workerTypesAdapter)
        binding.workersTypeTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Display the clicked item using toast
            Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }

    }
    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner, {
            it?.let {
                binding.progress.visibility=it
            }
        })
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(getResources().getColor(
                        R.color.teal))
                    .setActionTextColor(getResources().getColor(R.color.white)) .setAction("Ok")
                    {
                    }.show()
            }
        })
    }


}