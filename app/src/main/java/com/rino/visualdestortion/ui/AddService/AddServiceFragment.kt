package com.rino.visualdestortion.ui.AddService

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.rino.visualdestortion.R
import com.rino.visualdestortion.databinding.FragmentAddServiceBinding
import com.rino.visualdestortion.model.pojo.addService.AddServiceResponse
import com.rino.visualdestortion.model.pojo.addService.Districts
import com.rino.visualdestortion.model.pojo.addService.FormData
import com.rino.visualdestortion.model.pojo.addService.Streets
import com.rino.visualdestortion.ui.home.MainActivity
import com.rino.visualdestortion.utils.Constants
import com.rino.visualdestortion.utils.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AddServiceFragment : Fragment() {
    private lateinit var viewModel: AddServiceViewModel
    private lateinit var binding: FragmentAddServiceBinding
    private lateinit var addServiceResponse: AddServiceResponse
    private lateinit var sectorsList: ArrayList<String>
    private lateinit var municipalitesList: ArrayList<String>
    private lateinit var districtsList: ArrayList<String>
    private lateinit var streetList: ArrayList<String>
    private  var beforeImgBody: MultipartBody.Part? = null
    private  var duringImgBody: MultipartBody.Part? = null
    private  var afterImgBody: MultipartBody.Part? = null
    private lateinit  var formData: FormData
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val BEFORE_CAMERA_REQUEST_CODE = 100
    private val BEFORE_GALLERY_REQUEST_CODE = 200
    private val DURING_CAMERA_REQUEST_CODE = 300
    private val DURING_GALLERY_REQUEST_CODE = 400
    private val AFTER_CAMERA_REQUEST_CODE = 500
    private val AFTER_GALLERY_REQUEST_CODE = 600
    private val REQUEST_CODE = 100
    private var serviceTypeId = 1
    private var serviceName = "الكتابات المشوهة"
    private var lat = ""
    private var lng = ""
    private var isSectorSelected = false
    private var isMunicipalitySelected = false
    private var isDistrictSelected = false
    private var isStreetSelected = false
    private lateinit var  streetPopup :PopupWindow
    private lateinit var  districtPopup :PopupWindow
    private lateinit var  beforePopup :PopupWindow
    private lateinit var  duringPopup :PopupWindow
    private lateinit var  afterPopup  :PopupWindow
    private var fileName = "photo"
    private var currentPhotoPath = ""
    private lateinit var imageFile:File
    private lateinit var bitmap:Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity().application)
        viewModel = AddServiceViewModel(requireActivity().application)
        if (viewModel.isFirstTimeLaunch()) {
            viewModel.setFirstTimeLaunch(false)
            requestAllPermissions()
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            @SuppressLint("ResourceType")
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun requestAllPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_CODE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).bottomNavigation.isGone = true
        getLatestLocation()
       // (activity as MainActivity).bottomNavigation.
    }

    override fun onStop() {
        super.onStop()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun init() {
      //  getLatestLocation()
        setUpUI()
        initLists()
        observeData()
        registerConnectivityNetworkMonitor()
    }

    private fun setUpUI() {
        viewModel.getServicesData()
        if (getArguments() != null) {
            serviceName   = getArguments()?.get("serviceName").toString()
            serviceTypeId = getArguments()?.get("serviceID").toString().toInt()
            if (serviceTypeId == 6) {
                binding.textInputMSquare.isGone = true
            }
            else if (serviceTypeId == 5) {
                binding.textInputMCube.isGone = true
                binding.textInputNumberR.isGone = true
            }
            else if(serviceTypeId == 3)
            {
                binding.spicialItemsCard.isGone = true
                binding.spicialItemsTxt.isGone = true
            }
            else {
                binding.spicialItemsCard.isGone = true
                binding.spicialItemsTxt.isGone = true
                binding.duringPic.isGone = true
                binding.duringPicText.isGone = true
                binding.textInputDuringImg.isGone = true
            }
            binding.serviceTypeNameTxt.text = serviceName

        }
        binding.notesEditTxt.addTextChangedListener {
            val notesLength =  binding.notesEditTxt.text.toString().length
            binding.notesLength.text = "${Constants.convertNumsToArabic(notesLength.toString())} ${getString(R.string.chars)} "
        }
        binding.submitButton.setOnClickListener {
            submit()
        }
        binding.beforPic.setOnClickListener {
            val storageDirectory: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            try {
                imageFile = File.createTempFile(fileName,".jpg",storageDirectory)
                currentPhotoPath = imageFile.absolutePath
                val aymanCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val uri: Uri? = context?.let { it1 -> FileProvider.getUriForFile(it1,"com.rino.visualdestortion.fileprovider",imageFile) }
                aymanCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
                startActivityForResult(aymanCameraIntent,1)
            }catch(e:IOException){
                print(e.stackTrace)
            }
            //beforePicOnClick()
        }
        binding.afterPic.setOnClickListener {
            afterPicOnClick()
        }
        binding.duringPic.setOnClickListener {
            duringPicOnClick()
        }
    }

    private fun duringPicOnClick() {
        if (isExternalStoragePermissionGranted()&&isCameraPermissionGranted()) {
          //  showDuringPopup()
            enableGallery(AFTER_GALLERY_REQUEST_CODE)

        } else {
            navigateToAppSetting()
        }
    }

    private fun enableGallery(GALLERY_REQUEST_CODE:Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    private fun enableCamera(CAMERA_REQUEST_CODE:Int) {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val f = File(Environment.getExternalStorageDirectory(), "image.jpg")
//        if (f.exists()) {
//            f.delete()
//        }
//        val mImageCaptureUri = Uri.fromFile(f)
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun submit() {
        formData = getFormDataFromUi(serviceName)
           if (validateData(formData) && lat != "" && lng != "") {
//               val date =
//                   DateFormat.getDateInstance().format(Calendar.getInstance().time).toString()
               if(NetworkConnection.checkInternetConnection(requireContext())){
                    viewModel.setFormData(formData)
               }
               else{
                    showMessage(getString(R.string.no_internet))
               }
          //       viewModel.getDailyPreparationByServiceID(serviceTypeId.toString(), date)
           }
    }


    private fun getFormDataFromUi(serviceName: String): FormData {
        Log.e("Images","Before : ${beforeImgBody.toString()} ,During : ${duringImgBody.toString()} ,Aftar : ${afterImgBody.toString()}")
    //    Toast.makeText(requireContext(),"Before : ${beforeImgBody.toString()} ,During : ${duringImgBody.toString()} ,Aftar : ${afterImgBody.toString()}",Toast.LENGTH_SHORT).show()
        val formData = FormData()
        if (serviceName == "مخلفات الهدم") {
            if(binding.editTextMCube.text.toString()!="")
            formData.mCube = binding.editTextMCube.text.toString().toFloat()
            if(binding.editTextNumberR.text.toString()!="")
            formData.numberR = binding.editTextNumberR.text.toString().toInt()
        } else if (serviceName == "الكتابات المشوهة")
            if(binding.editTextMSquare.text.toString()!="")
            formData.mSquare = binding.editTextMSquare.text.toString().toInt()

        formData.serviceTypeId = serviceTypeId.toString()
        formData.sectorName = binding.sectorTextView.text.toString()
        formData.municipalityName = binding.municipalitesTextView.text.toString()
        formData.districtName = binding.districtsTextView.text.toString()
        formData.streetName = binding.streetTextView.text.toString()
        formData.lat = lat
        formData.lng = lng
        formData.notes = binding.notesEditTxt.text.toString()
        if(beforeImgBody != null) {

            formData.beforeImg = beforeImgBody as MultipartBody.Part

        }
        Log.e("Image","Before : ${beforeImgBody.toString()}  ,Aftar : ${afterImgBody.toString()}")
        if(afterImgBody != null) {
            formData.afterImg = afterImgBody as MultipartBody.Part
        }
        if(duringImgBody != null) {
            formData.duringImg = duringImgBody as MultipartBody.Part
        }

        return formData
    }

    private fun afterPicOnClick() {
        if (isExternalStoragePermissionGranted()&&isCameraPermissionGranted()) {
        //    showAfterPopup()
            enableGallery(AFTER_GALLERY_REQUEST_CODE)
        } else {
            navigateToAppSetting()
        }
    }

    private fun beforePicOnClick() {
        if (isExternalStoragePermissionGranted()&&isCameraPermissionGranted()) {
        //    showBeforePopup()
            enableGallery(BEFORE_GALLERY_REQUEST_CODE)
        } else {
            navigateToAppSetting()
        }
    }
    private fun showBeforePopup(){
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.select_gallery_or_camera_view, null)
        beforePopup = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        beforePopup.isOutsideTouchable = true
        beforePopup.isFocusable = true
        binding.frameLayout.alpha = 0.3f
        beforePopup.setOnDismissListener { binding.frameLayout.alpha = 1f }
        beforePopup.animationStyle = R.anim.down_to_up
        beforePopup.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
        beforePopup.showAtLocation(requireView(), Gravity.BOTTOM, 0, 0)
        val gallary_img = view.findViewById<ImageView>(R.id.gallary_img)
        gallary_img.setOnClickListener{
            beforePopup.dismiss()
            enableGallery(BEFORE_GALLERY_REQUEST_CODE)
        }
        val camera_img = view.findViewById<ImageView>(R.id.camera_img)
        camera_img.setOnClickListener{
            beforePopup.dismiss()
           enableCamera(BEFORE_CAMERA_REQUEST_CODE)
        }
    }

    private fun showDuringPopup(){
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.select_gallery_or_camera_view, null)
        duringPopup = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        duringPopup.isOutsideTouchable = true
        binding.frameLayout.alpha = 0.3f
        duringPopup.setOnDismissListener { binding.frameLayout.alpha = 1f }
        duringPopup.isFocusable = true
        duringPopup.animationStyle = R.anim.down_to_up
        duringPopup.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
        duringPopup.showAtLocation(requireView(), Gravity.BOTTOM, 0, 0)
        val gallary_img = view.findViewById<ImageView>(R.id.gallary_img)
        gallary_img.setOnClickListener{
            duringPopup.dismiss()
            enableGallery(DURING_GALLERY_REQUEST_CODE)
        }
        val camera_img = view.findViewById<ImageView>(R.id.camera_img)
        camera_img.setOnClickListener{
            duringPopup.dismiss()
            enableCamera(DURING_CAMERA_REQUEST_CODE)
        }
    }

    private fun showAfterPopup(){
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.select_gallery_or_camera_view, null)
        binding.frameLayout.alpha = 0.3f
        afterPopup = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        afterPopup.setOnDismissListener { binding.frameLayout.alpha = 1f }
        afterPopup.isOutsideTouchable = true
        afterPopup.isFocusable = true
        afterPopup.animationStyle = R.anim.down_to_up
        afterPopup.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
        afterPopup.showAtLocation(requireView(), Gravity.BOTTOM, 0, 0)
        val gallary_img = view.findViewById<ImageView>(R.id.gallary_img)
        gallary_img.setOnClickListener{
            afterPopup.dismiss()
            enableGallery(AFTER_GALLERY_REQUEST_CODE)
        }
        val camera_img = view.findViewById<ImageView>(R.id.camera_img)
        camera_img.setOnClickListener{
            afterPopup.dismiss()
            enableCamera(AFTER_CAMERA_REQUEST_CODE)
        }
    }

    private fun initLists() {
        sectorsList = ArrayList()
        municipalitesList = ArrayList()
        districtsList = ArrayList()
        streetList = ArrayList()

    }

    private fun observeData() {
        observeGetServicesData()
        observeSetFormData()
        observeLoading()
        observeShowError()
        observeSelectedStreet()
        observeSelectedDistrict()
        ObserveIsStreetSelected()
        ObserveIsDistrictSelected()
    }

    private fun ObserveIsDistrictSelected() {
        viewModel.isSelectedDistrict.observe(viewLifecycleOwner) {
            it?.let {
                isDistrictSelected = it
            }
        }
    }

    private fun ObserveIsStreetSelected() {
        viewModel.isSelectedStreet.observe(viewLifecycleOwner) {
            it?.let {
                isStreetSelected = it
            }
        }
    }

    private fun observeSelectedStreet() {
        viewModel.selectedStreet.observe(viewLifecycleOwner) {
            it?.let {
                if(streetPopup.isShowing) {
                    streetPopup.dismiss()
                }
                binding.streetTextView.setText(it)
            }
        }
    }

    private fun observeSelectedDistrict() {
        viewModel.selectedDistrict.observe(viewLifecycleOwner) {
            it?.let {
                if(districtPopup.isShowing) {
                    districtPopup.dismiss()
                }
                binding.districtsTextView.setText(it.name)
                val list =  it.streets
                setStreetPopup(list)
            }
        }
    }
    private fun observeSetFormData() {
        viewModel.setServiceForm.observe(viewLifecycleOwner) {
            it?.let {
                navigateToQRCode(it.qrCode.toString())
            }
        }
    }

    private fun navigateToQRCode(qrCode: String) {
        val action = AddServiceFragmentDirections.actionAddServiceToQRCode(qrCode,serviceName,serviceTypeId.toString())
        findNavController().navigate(action)
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
        setSectorsMenuItems()
    }

    private fun setSectorsMenuItems() {
        sectorsList.clear()
        binding.sectorTextView.setText(R.string.sector)
        for (sector in addServiceResponse.sectors!!) {
            sectorsList.add(sector.name.toString())
        }
        val sectorsAdapter = ArrayAdapter(
            requireContext(), R.layout.dropdown_item,
            sectorsList
        )
        binding.sectorTextView.setAdapter(sectorsAdapter)
        binding.sectorTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
         //       val selectedItem = parent.getItemAtPosition(position).toString()
                isSectorSelected = true
                setMunicipalitesMenuItems(position)
            }

    }

    private fun setMunicipalitesMenuItems(posSector: Int) {
        binding.municipalitesTextView.setText(R.string.municipalites)
        municipalitesList.clear()
        for (municipalite in addServiceResponse.sectors?.get(posSector)?.municipalites!!) {
            municipalitesList.add(municipalite.name.toString())
        }
        val municipalitesAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            municipalitesList
        )
        binding.municipalitesTextView.setAdapter(municipalitesAdapter)
        binding.municipalitesTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
       //         val selectedItem = parent.getItemAtPosition(position).toString()
                isMunicipalitySelected = true
     //           setDistrictsMenuItems(posSector, position)
                val list =  addServiceResponse.sectors?.get(posSector)?.
                municipalites?.get(position)?.districts
                setDistrictPopup(list)
            }
    }

    private fun setDistrictPopup(list:ArrayList<Districts>?) {
        binding.districtsTextView.setOnClickListener {
            districtPopup = showDistrictPopup(list?: arrayListOf())
        }
    }

    private fun showDistrictPopup(list: List<Districts>): PopupWindow {
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.text_popup_view, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.textRecycle)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        val adapter = DistrictAdapter(arrayListOf(),viewModel)
        recyclerView.adapter = adapter
        adapter.updateItems(list)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val searchView = view.findViewById<SearchView>(R.id.mSearch)
        searchView.setQueryHint(getString(R.string.districts))
        searchDistrict(searchView,list,adapter)
        Log.e("ItemCount", adapter.getItemCount().toString())
        streetPopup = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        streetPopup.isOutsideTouchable = true
        streetPopup.isFocusable = true
        streetPopup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        streetPopup.showAtLocation(requireView(), Gravity.CENTER, 0, 0);
        return  streetPopup
    }

    private fun searchDistrict(searchView: SearchView, list: List<Districts>, adapter: DistrictAdapter) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    filterDistrict(query, list, adapter)
                }
                return false
            }
        })
    }

    private fun filterDistrict(query: String, list: List<Districts>, adapter: DistrictAdapter) {
        val filteredList: ArrayList<Districts> = ArrayList()
        if (list?.isNotEmpty() == true) {
            for (item in list!!) {
                if (item.name?.toLowerCase()?.contains(query.toLowerCase()) == true) {
                    filteredList.add(item)
                }
            }
            adapter.updateItems(filteredList)
        }
    }

    fun setStreetPopup(steetArrayList: ArrayList<Streets>?){
        binding.streetTextView.setOnClickListener {
            steetArrayList?.let { it1 -> setListOfStreets(it1) }
            streetPopup = showStreetPopup()
        }

    }
    fun setListOfStreets(streetArrayList: ArrayList<Streets>?){
        streetList.clear()
        if (streetArrayList != null) {
            for (street in streetArrayList ){
                streetList.add(street.name.toString())
            }
        }
    }
    private fun showStreetPopup(): PopupWindow {
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.text_popup_view, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.textRecycle)
     //   recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        Log.e("streetList",streetList.toString())
        val adapter = StreetAdapter(arrayListOf(),viewModel)
        recyclerView.adapter = adapter
        adapter.updateItems(streetList)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val searchView = view.findViewById<SearchView>(R.id.mSearch)
        searchView.setQueryHint(getString(R.string.street))
        searchStreet(searchView,streetList,adapter)
        Log.e("ItemCount", adapter.getItemCount().toString())

        streetPopup = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        streetPopup.isOutsideTouchable = true
        streetPopup.isFocusable = true
        streetPopup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        streetPopup.showAtLocation(requireView(), Gravity.CENTER, 0, 0);
        return  streetPopup
    }

    private fun searchStreet(searchView: SearchView,streetArrayList: ArrayList<String>?,adapter:StreetAdapter) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    filterStreet(query, streetArrayList, adapter)
                }
                return false
            }
        })

    }
        fun filterStreet(text: String,list: ArrayList<String>?,adapter: StreetAdapter) {
            val filteredList: ArrayList<String> = ArrayList<String>()
            if (list?.isNotEmpty() == true) {
                for (item in list!!) {
                    if (item.toLowerCase()?.contains(text.toLowerCase()) == true) {
                        filteredList.add(item)
                    }
                }
                adapter.updateItems(filteredList)
            }
        }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun validateData(formData: FormData): Boolean {
        var flagSector = true
        var flagDistrict = true
        var flagMunicipality = true
        var flagStreet = true
        var flagBeforeImg = true
        var flagDuringImg = true
        var flagAfterImg = true
        var flagNumberR = true
        var flagMCube = true
        var flagMSquare = true
        var flagNotes = true
        //  Toast.makeText(requireContext(),"Selected : ${formData.sectorName == R.string.sector.toString()}",Toast.LENGTH_SHORT).show()
        if (!isSectorSelected) {
            binding.sectorTextInputLayout.error = getString(R.string.required_field)
            flagSector = false
        } else {
            binding.sectorTextInputLayout.error = null
            binding.sectorTextInputLayout.isErrorEnabled = false
            flagSector = true
        }

        if (!isDistrictSelected) {
            binding.districtsTextInputLayout.error = getString(R.string.required_field)
            flagDistrict = false
        } else {
            binding.districtsTextInputLayout.error = null
            binding.districtsTextInputLayout.isErrorEnabled = false
            flagDistrict = true
        }
        if (!isMunicipalitySelected) {
            binding.municipalitesTextInputLayout.error = getString(R.string.required_field)
            flagMunicipality = false
        } else {
            binding.municipalitesTextInputLayout.error = null
            binding.municipalitesTextInputLayout.isErrorEnabled = false
            flagMunicipality = true
        }
        if (!isStreetSelected) {
            binding.streetTextInputLayout.error = getString(R.string.required_field)
            flagStreet = false
        } else {
            binding.streetTextInputLayout.error = null
            binding.streetTextInputLayout.isErrorEnabled = false
            flagStreet = true
        }
        //binding.beforPic.drawable == resources.getDrawable(R.drawable.picture)
        // Toast.makeText(requireContext(),"Before : ${beforeImgBody == null}  ,Aftar : ${afterImgBody == null}",Toast.LENGTH_SHORT).show()
        if (beforeImgBody == null) {
            binding.textInputbeforeImg.error = getString(R.string.required)
            flagBeforeImg = false
        } else {
            binding.textInputbeforeImg.error = null
            binding.textInputbeforeImg.isErrorEnabled = false
            flagBeforeImg = true
        }
        if (afterImgBody == null) {
            binding.textInputAfterImg.error = getString(R.string.required)
            flagAfterImg = false
        } else {
            binding.textInputAfterImg.error = null
            binding.textInputAfterImg.isErrorEnabled = false
            flagAfterImg = true
        }

        if (formData.notes != null) {
            if (formData.notes!!.length > 500) {
                binding.textInputNotes.error = "هذا العنصر يجب ان يكون أقل من 500 حرف  "
                flagNotes = false
            } else {
                binding.textInputNotes.error = null
                binding.textInputNotes.isErrorEnabled = false
                flagNotes = true
            }
        }
        if (serviceTypeId == 6) {
            if (formData.mCube == null) {
                binding.textInputMCube.error = getString(R.string.required_field)
                flagMCube = false
            } else {
                binding.textInputMCube.error = null
                binding.textInputMCube.isErrorEnabled = false
                flagMCube = true
            }
            if (formData.numberR == null) {
                binding.textInputNumberR.error = getString(R.string.required_field)
                flagNumberR = false
            } else {
                binding.textInputNumberR.error = null
                binding.textInputNumberR.isErrorEnabled = false
                flagNumberR = true
            }
            if (duringImgBody == null) {
                binding.textInputDuringImg.error = getString(R.string.required)
                flagDuringImg = false
            } else {
                binding.textInputDuringImg.error = null
                binding.textInputDuringImg.isErrorEnabled = false
                flagDuringImg = true
            }
        } else if (serviceTypeId == 5) {
            if (formData.mSquare == null) {
                binding.textInputMSquare.error = getString(R.string.required_field)
                flagMSquare = false
            } else {
                binding.textInputMSquare.error = null
                binding.textInputMSquare.isErrorEnabled = false
                flagMSquare = true
            }
            if (duringImgBody == null) {
                binding.textInputDuringImg.error = getString(R.string.required)
                flagDuringImg = false
            } else {
                binding.textInputDuringImg.error = null
                binding.textInputDuringImg.isErrorEnabled = false
                flagDuringImg = true
            }
        } else if (serviceTypeId == 3) {
            if (duringImgBody == null) {
                binding.textInputDuringImg.error = getString(R.string.required)
                flagDuringImg = false
            } else {
                binding.textInputDuringImg.error = null
                binding.textInputDuringImg.isErrorEnabled = false
                flagDuringImg = true
            }

        }
        return (flagSector && flagDistrict && flagMunicipality && flagStreet &&
                flagAfterImg && flagBeforeImg && flagDuringImg &&
                flagMCube && flagMSquare && flagNumberR &&
                flagNotes
                  )
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
            showMessage(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK ){
            bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            val out = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 30, out)
            val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
            binding.afterPic.setImageBitmap(bitmap)
            binding.beforPic.setImageBitmap(bitmap)
            binding.duringPic.setImageBitmap(bitmap)

            setDuringImage(bitmap)
            setAfterImage(bitmap)
            setBeforeImage(bitmap)

        }else{
            return
        }
        /*if (resultCode == Activity.RESULT_OK && requestCode == AFTER_CAMERA_REQUEST_CODE && data != null) {
            val bitmap = data.extras?.get("data") as Bitmap
            binding.afterPic.setImageBitmap(bitmap)
           val afterBitmap = resizeBitmap(getRealPathFromURI(getImageUri(requireContext(), bitmap,"AFTER_IMG")))
            setAfterImage(afterBitmap)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == AFTER_GALLERY_REQUEST_CODE && data != null) {
            binding.afterPic.setImageURI(data?.data) // handle chosen image
            //    var bitmap = data?.data as Bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireContext().getContentResolver(),
                data?.data
            )
            setAfterImage(bitmap)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == BEFORE_GALLERY_REQUEST_CODE && data !=null) {
            binding.beforPic.setImageURI(data?.data) // handle chosen image
            //    var bitmap = data?.data as Bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireContext().getContentResolver(),
                data?.data
            )
            setBeforeImage(bitmap)
            }

        if (resultCode == Activity.RESULT_OK && requestCode == BEFORE_CAMERA_REQUEST_CODE && data != null) {
            val bitmap = data.extras?.get("data") as Bitmap
            binding.beforPic.setImageBitmap(bitmap)
            val beforeBitmap = resizeBitmap(getRealPathFromURI(getImageUri(requireContext(), bitmap,"BEFORE_IMG")))
            setBeforeImage(beforeBitmap)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == DURING_GALLERY_REQUEST_CODE&& data != null) {
            binding.duringPic.setImageURI(data?.data) // handle chosen image
            //    var bitmap = data?.data as Bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireContext().getContentResolver(),
                data?.data
            )
            setDuringImage(bitmap)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DURING_CAMERA_REQUEST_CODE && data != null) {
            val bitmap = data.extras?.get("data") as Bitmap
            binding.duringPic.setImageBitmap(bitmap)
            val duringBitmap = resizeBitmap(getRealPathFromURI(getImageUri(requireContext(), bitmap,"DURING_IMG")))
            setDuringImage(duringBitmap)
        }*/
        }

    private fun setDuringImage(bitmap: Bitmap) {
        var duringBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        CoroutineScope(Dispatchers.Default).launch {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val formatted = current.format(formatter)
            duringBitmap =
                drawTextToBitmap(
                    duringBitmap,
                    formatted.toString()
                )
            duringBitmap = compressBitmap(duringBitmap,30)
            try {

//                val file = File(
//                    getRealPathFromURI(data?.data)
//                )
                val file =
                    File(getRealPathFromURI(getImageUri(requireContext(), duringBitmap,"DURING_IMG_")))
                println("duringFilePath" + file.path)
                val requestFile: RequestBody =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                duringImgBody = MultipartBody.Part.createFormData(
                    "duringImg",
                    file.name.trim(),
                    requestFile
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        binding.duringPic.setImageBitmap(duringBitmap)
    }

    private fun setBeforeImage(bitmap: Bitmap) {
        var beforeBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        CoroutineScope(Dispatchers.Default).launch {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val formatted = current.format(formatter)
            beforeBitmap =
                drawTextToBitmap(
                    beforeBitmap,
                    formatted.toString()
                )
            beforeBitmap = compressBitmap(beforeBitmap,30)

            try {

//                val file = File(
//                    getRealPathFromURI(data?.data)
//                )
                val file =
                    File(getRealPathFromURI(getImageUri(requireContext(), beforeBitmap,"bEFORE_IMG")))
//                        println("beforefilePath" + file.path)
                val requestFile: RequestBody =
                    file
                        .asRequestBody("multipart/form-data".toMediaTypeOrNull())
                beforeImgBody = MultipartBody.Part.createFormData(
                    "beforeImg",
                    file.name.trim(),
                    requestFile
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        binding.beforPic.setImageBitmap(beforeBitmap)
    }

    private fun setAfterImage(bitmap: Bitmap) {
        var afterBitmap =bitmap.copy(Bitmap. Config.ARGB_8888,true)
        CoroutineScope(Dispatchers.Default).launch {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val formatted = current.format(formatter)
            afterBitmap =
                drawTextToBitmap(
                    afterBitmap,
                    formatted.toString()
                )
            afterBitmap = compressBitmap(afterBitmap,30)
            try {
                val file = File(getRealPathFromURI(getImageUri(requireContext(), afterBitmap,"AFTER_IMG")))
                println("afterfilePath" + file.path)
                val requestFile: RequestBody =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                afterImgBody = MultipartBody.Part.createFormData(
                    "afterImg",
                    file.name.trim(),
                    requestFile
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        binding.afterPic.setImageBitmap(afterBitmap)
    }
private  fun  resizeBitmap(imagePath:String?):Bitmap{
    val targetW = 800
    val targetH = 1000

    val bmOptions = BitmapFactory.Options()
    bmOptions.inJustDecodeBounds = true
    BitmapFactory.decodeFile(imagePath, bmOptions)
    val photoW = bmOptions.outWidth
    val photoH = bmOptions.outHeight

    val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

    bmOptions.inJustDecodeBounds = false
    bmOptions.inSampleSize = scaleFactor
    bmOptions.inPurgeable = true

   return BitmapFactory.decodeFile(imagePath, bmOptions)
}
    @SuppressLint("ResourceAsColor")
    private fun drawTextToBitmap(bitmap: Bitmap, text: String): Bitmap {

        val canvas = Canvas(bitmap)
        // new antialised Paint - empty constructor does also work
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val width: Int = bitmap.width
        val  height: Int = bitmap.height

        // text size in pixels

        paint.textSize = width * .05f

        //custom fonts or a default font
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val fm: Paint.FontMetrics = Paint.FontMetrics()
    //    val color = ContextCompat.getColor(requireContext(), R.color.transparent_black)
        paint.setARGB(50,51,26,24)
        paint.getFontMetrics(fm)
        val margin = 5f
        canvas.drawRect(
            0f, 0f,
            width.toFloat(), width*.10f,paint
        )
        val original = BitmapFactory.decodeResource(resources, R.drawable.splash_icon)
        canvas.drawBitmap(original,null,RectF(width*.02f,width*.02f,width*.10f,width*.10f),null)
        // draw text to the Canvas center
        val bounds = Rect()
        //draw the text
        paint.getTextBounds(text, 0, text.length, bounds)
        paint.color = Color.WHITE
        canvas.drawText(text, width*.15f, width*.07f, paint)

        return bitmap
    }

    private fun compressBitmap(bitmap: Bitmap, quality:Int):Bitmap{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, quality, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap,title:String): Uri? {
        val bytes = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            inImage.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 20, bytes)
        }
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            title+ currentDate.toString().replace(" ",""),
            null
        )
//        val path = MediaStore.Images.Media.insertImage(
//            inContext.contentResolver,
//            inImage,
//            "After_IMG_",
//            null
//        )
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        val cursor: Cursor? =
            uri?.let { requireActivity().getContentResolver().query(it, null, null, null, null) }
        cursor?.moveToFirst()
        val idx: Int? = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return idx?.let { cursor.getString(it) }
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
                enableLocationPermission()
            }
        } else {
          //    navigateToAppSetting()
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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            lng = location.longitude.toString()
            lat = location.latitude.toString()
            //   Toast.makeText(context, "lat:" + lat + ", lng:" + lng, Toast.LENGTH_SHORT).show()

        }
    }

    private fun enableLocationPermission() {
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

//    //camera permission
//    @SuppressLint("MissingPermission")
//    fun checkCameraPermission() {
//        if (!isCameraPermissionGranted()) {
//            navigateToAppSetting()
//        } else {
//           sho
//        }
//    }

    private fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity().application,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun navigateToAppSetting() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", requireContext().packageName, null)
        })
    }


//
//    //external storage
//    @SuppressLint("MissingPermission")
//    fun checkExternalStoragePermission() {
//        if (!isExternalStoragePermissionGranted()) {
//            navigateToAppSetting()
//        } else {
//            showBeforePopup()
//        }
//    }

//    private fun enablePhotoes() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, REQUEST_CODE)
//    }

    private fun isExternalStoragePermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity().application,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun showMessage(msg: String) {
        lifecycleScope.launchWhenResumed {
            Snackbar.make(requireView(), msg, Snackbar.LENGTH_INDEFINITE)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                    resources.getColor(
                        R.color.teal
                    )
                )
                .setActionTextColor(resources.getColor(R.color.white))
                .setAction(getString(R.string.dismiss))
                {
                }.show()
        }
    }
    private fun registerConnectivityNetworkMonitor() {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
//                    if (activity != null) {
//                        activity!!.runOnUiThread {
//                            showMessage(getString(R.string.internet))                        }
//                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    if (activity != null) {
                        activity!!.runOnUiThread {
//                                Toast.makeText(
//                                    context, "لا يوجد انترنت ",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            showMessage(getString(R.string.no_internet))                        }
                    }
                }
            }
        )
    }
//    val requestExternalStoragePermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (!isGranted) {
//
//            } else {
//
//            }
//        }


}
