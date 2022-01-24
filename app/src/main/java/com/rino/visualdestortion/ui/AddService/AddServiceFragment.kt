package com.rino.visualdestortion.ui.AddService

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentAddServiceBinding
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.ui.home.MainActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


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
    private lateinit var equipmentsAdapter: EquipmentsAdapter
    private lateinit var workerTypesAdapter: WorkerTypesAdapter
    private lateinit var equipmentsMap: HashMap<Int?,Int?>
    private lateinit var workersTypeMap: HashMap<Int?,Int?>
    private lateinit var equipmentsCountList: ArrayList<EquipmentItem>
    private lateinit var workerTypesCountList: ArrayList<EquipmentItem>
    private lateinit var equipmentsCountMap: HashMap<Long?,Int?>
    private lateinit var workerTypesCountMap: HashMap<Long?,Int?>

    private lateinit var beforeImgBody:MultipartBody.Part
    private lateinit var afterImgBody:MultipartBody.Part
    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private val CAMERA_REQUEST_CODE = 200
    private val REQUEST_CODE = 100
    private var serviceTypeId = ""
    private var lat = ""
    private var lng = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().application)
        getLatestLocation()
        requestPermissionLauncher.launch((Manifest.permission.READ_EXTERNAL_STORAGE))
        requestPermissionLauncher.launch((Manifest.permission.CAMERA))

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
    override fun onPause() {
        super.onPause()
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun init() {
        setUpUI()
        initLists()
        observeData()

    }

    private fun setUpUI() {
   //      requestPermissionLauncher.launch((Manifest.permission.CAMERA))
    //    requestPermissionLauncher.launch((Manifest.permission.WRITE_EXTERNAL_STORAGE))

        equipmentsAdapter = EquipmentsAdapter(arrayListOf(),viewModel)
        workerTypesAdapter = WorkerTypesAdapter(arrayListOf(),viewModel)
        binding.equipmentsRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext() )
            adapter = equipmentsAdapter
        }
        binding.workersTypeRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext() )
            adapter = workerTypesAdapter
        }
        viewModel.getServicesData()
        binding.beforPic.setOnClickListener({
            beforePicOnClick()
        })
        binding.afterPic.setOnClickListener({
            afterPicOnClick()
        })
        binding.submitButton.setOnClickListener({

            if(lat!=""&&lng!=""&&validateData())

             viewModel.setFormData( getFormDataFromUi())

        })
        if(getArguments() != null) {

            val serviceName = getArguments()?.get("serviceName").toString()
            if(serviceName.equals("مخلفات الهدم")){
                binding.textInputMSquare.isGone = true
            }
            else if(serviceName.equals("الكتابات المشوهة")){
                binding.textInputMCube.isGone = true
                binding.textInputNumberR.isGone = true
            }
            else{
                binding.spicialItemsCard.isGone = true
                binding.spicialItemsTxt.isGone = true
            }
            binding.serviceTypeNameTxt.text = serviceName
            serviceTypeId = getArguments()?.get("serviceID").toString()
        }
    }

    private fun getFormDataFromUi():FormData {
       var formData = FormData()
        formData.serviceTypeId = serviceTypeId
        formData.sectorName= binding.sectorTextView.text.toString()
        formData.municipalityName=binding.municipalitesTextView.text.toString()
        formData.districtName=binding.districtsTextView.text.toString()
        formData.streetName=binding.streetTextView.text.toString()
        formData.lat = lat
        formData.lng = lng
        formData.WorkersTypesList = workerTypesAdapter.getWorkerTypesMap()
        formData.equipmentList = equipmentsAdapter.getEquipmentMap()
        formData.notes         = binding.notesEditTxt.text.toString()
        formData.mSquare = binding.editTextMSquare.text.toString().toInt()
        formData.mCube = binding.editTextMCube.text.toString().toInt()
        formData.numberR = binding.editTextNumberR.text.toString().toInt()
        formData.beforeImg     = beforeImgBody
        formData.afterImg      = afterImgBody
        formData.percentage    = binding.precentageEditTxt.text.toString()
        return  formData
//        Log.e("serviceTypeId",formData.serviceTypeId)
//        Log.e("Sectors",formData.sectorName)
//        Log.e("municipalites",formData.municipalityName)
//        Log.e(".districts",formData.districtName)
//        Log.e("street", formData.streetName)
//        Log.e("lat",formData.lat)
//        Log.e("lng",formData.lng)
//        Log.e("workerTypes",formData.WorkersTypesList.toString())
//        Log.e("equipments",formData.equipmentList.toString())
//        Log.e("beforeImg", formData.beforeImg.toString())
//        Log.e("afterImg", formData.afterImg.toString())
//        Log.e("mSquare", formData.mSquare.toString()!!)
//        Log.e("mCube", formData.mCube.toString()!!)
//        Log.e("numberR", formData.numberR.toString()!!)
//        Log.e("notes", formData.notes!!)
//        Log.e("percentage", formData.percentage!!)

    }

    private fun afterPicOnClick() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun beforePicOnClick() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)

        }

    private fun initLists() {
        sectorsList = ArrayList()
        municipalitesList = ArrayList()
        districtsList = ArrayList()
        streetList = ArrayList()
        equipmentList = ArrayList()
        workersTypeList = ArrayList()
        equipmentsMap = HashMap()
        workersTypeMap = HashMap()
        equipmentsCountList = ArrayList()
        workerTypesCountList = ArrayList()
        equipmentsCountMap =  HashMap()
        workerTypesCountMap = HashMap()
    }

    private fun observeData() {
        observeGetServicesData()
        observeSetFormData()
        observeLoading()
        observeShowError()
        observeDeletedEquipmentItem()
        observeDeletedWorkerTypeItem()
    }

    private fun observeSetFormData() {
        viewModel.setServiceForm.observe(viewLifecycleOwner, {
            it?.let {
                navigateToQRCode(it.qrCode.toString())
            }
        })
    }

    private fun navigateToQRCode(qrCode:String) {
        val action = AddServiceFragmentDirections.actionAddServiceToQRCode(qrCode)
        findNavController().navigate(action)
    }

    private fun observeDeletedWorkerTypeItem() {
        viewModel.workerTypeDeleteItem.observe(viewLifecycleOwner, {
            it?.let {
                workerTypesCountList.remove(it)
            }
        })
    }

    private fun observeDeletedEquipmentItem() {
        viewModel.equipmentsDeleteItem.observe(viewLifecycleOwner, {
            it?.let {
                equipmentsCountList.remove(it)
            }
        })
    }

    private fun observeGetServicesData() {
            viewModel.getServicesData.observe(viewLifecycleOwner, {
                it.let {
                    addServiceResponse = it
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
          //  Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
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
          //  Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
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
         //   Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }
    }
    private fun validateData(): Boolean {
    var flag = true
         if (binding.sectorTextView.text.toString().equals(R.string.sector.toString())) {
             binding.sectorTextInputLayout.error = "برجاء ادخال هذا العنصر"
             flag = false
         }

         if (binding.districtsTextView.text.toString() === R.string.districts.toString()) {
             binding.districtsTextInputLayout.error = "برجاء ادخال هذا العنصر"
             flag = false
         }
        if (binding.municipalitesTextView.text.toString() === R.string.municipalites.toString()) {
            binding.municipalitesTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flag = false
        }
        if (binding.streetTextView.text.toString() === R.string.street.toString()) {
            binding.streetTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flag = false
        }
        if (equipmentsCountList.size == 0) {
            binding.equipmentsTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flag = false
        }
        if (workerTypesCountList.size == 0) {
            binding.workersTypeTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flag = false
        }
        if (binding.precentageEditTxt.text.toString().isEmpty() ){
            binding.textInputPercentage.error = "برجاء ادخال هذا العنصر"
            flag = false
        }
        if (binding.beforPic.resources.equals(R.drawable.picture)){
            binding.workersTypeTextInputLayout.error = "برجاء ادخال هذا العنصر"
            flag = false
        }
        return flag
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
           // Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setEquipmentsMenuItems() {
        equipmentList.clear()
        var index = 0
        binding.equipmentsTextView.setText(R.string.select)
        for(equipment in addServiceResponse.equipment!!){
            equipmentList.add(equipment.name.toString())
            equipmentsMap[index] = equipment.id
            index++
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
            equipmentList)
        binding.equipmentsTextView.setAdapter(adapter)
        binding.equipmentsTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            var item = equipmentsMap[position]?.toLong()?.let { EquipmentItem(selectedItem, it,1) }
            if (item != null) {
                equipmentsCountList.add(item)
            }
            equipmentsAdapter.updateItems(equipmentsCountList)
            // Display the clicked item using toast
            //Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }

    }

    private fun setWorkersTypeMenuItems() {
        workersTypeList.clear()
        var index= 0
        binding.workersTypeTextView.setText(R.string.select)
        for(workerType in addServiceResponse.workerTypes!!){
            workersTypeList.add(workerType.name.toString())
            workersTypeMap[index] = workerType.id
            index++
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,
            workersTypeList)
        binding.workersTypeTextView.setAdapter(adapter)
        binding.workersTypeTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            var item = workersTypeMap[position]?.toLong()?.let { EquipmentItem(selectedItem, it,1) }
            if (item != null) {
                workerTypesCountList.add(item)
            }
            workerTypesAdapter.updateItems(workerTypesCountList)
            // Display the clicked item using toast
            //Toast.makeText(requireContext(),"Selected : $selectedItem",Toast.LENGTH_SHORT).show()
        }

    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner, {
            it?.let {
                binding.progress.visibility = it
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE && data != null) {
            binding.afterPic.setImageBitmap(data.extras?.get("data") as Bitmap)
            var bitmap = data.extras?.get("data") as Bitmap
            try {
                val file = File(getRealPathFromURI(getImageUri(requireContext(),bitmap)!!))
                println("filePath" + data?.data?.path)
                if (file != null) {
                    val requestFile: RequestBody =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
                    afterImgBody = MultipartBody.Part.createFormData(
                        "afterImg",
                        file?.name.trim(),
                        requestFile
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            binding.beforPic.setImageURI(data?.data) // handle chosen image
            try {

                val file = File(
                getRealPathFromURI(data?.data!!)
                )
                println("filePath" + data?.data?.path)
                if (file != null) {
                    val requestFile: RequestBody =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
                    beforeImgBody = MultipartBody.Part.createFormData(
                        "beforeImg",
                        file?.name.trim(),
                        requestFile
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri): String? {
        val cursor: Cursor? = requireActivity().getContentResolver().query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val idx: Int? = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return idx?.let { cursor.getString(it) }
    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }



    @SuppressLint("MissingPermission")
    fun getLatestLocation() {
        if (isPermissionGranted()) {
            if (checkLocation()) {
                val locationRequest = LocationRequest()
                with(locationRequest) {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 1000
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {

                enableLocation()
            }
        } else {
            requestPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity().application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireActivity().application,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocation(): Boolean {
        val locationManager =
            requireActivity().application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true
        } else {
            return false;
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            // TODO use current location long and lat
             lng = location.longitude.toString()
             lat = location.latitude.toString()
         // Toast.makeText(context, "lat:" + lat + ", lng:" + lng, Toast.LENGTH_SHORT).show()

        }
    }


    private fun enableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )
    }
}
