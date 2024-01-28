package com.msandypr.thesandynews.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.msandypr.thesandynews.R
import com.msandypr.thesandynews.databinding.FragmentQrCodeBinding

class QrCodeFragment : Fragment() {

    private lateinit var cameraButton: MaterialButton
    private lateinit var galleryButton: MaterialButton
    private lateinit var imageViewToScan: ImageView
    private lateinit var scanButton: MaterialButton
    private lateinit var resultTv: TextView
    private lateinit var binding: FragmentQrCodeBinding

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101

        private const val TAG = "MAIN_TAG"
    }

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>

    private var imageUri: Uri? = null

    private var barcodeScannerOptions: BarcodeScannerOptions? = null
    private var barcodeScanner: BarcodeScanner? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraButton = binding.cameraButton
        galleryButton = binding.galleryButton
        imageViewToScan = binding.imageViewToScan
        scanButton = binding.scanButton
        resultTv = binding.resultTv

        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        barcodeScannerOptions = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions!!)

        cameraButton.setOnClickListener {
            if (checkCameraPermission()){
                pickImageCamera()
            } else {
                requestCameraPermission()
            }
        }

        galleryButton.setOnClickListener {
            if (checkStoragePermission()){
                pickImageGallery()
            } else {
                requestStoragePermission()
            }
        }

        scanButton.setOnClickListener {
            if (imageUri == null){
                showToast("Pilih gambarnya dulu sayang")
            } else {
                detectResultFromImage()
            }
        }

    }

    private fun detectResultFromImage() {
        Log.d(TAG, "detectResultFromImage: ")
        try {
            val inputImage = InputImage.fromFilePath(requireContext(), imageUri!!)
            val barcodeResult = barcodeScanner!!.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    extractBarcodeQrCodeInfo(barcodes)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "detectResultFromImage: ", e)
                    showToast("Failed scanning due to ${e.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "detectResultFromImage: ", e)
            showToast("Failed due to ${e.message}")
        }
    }


    @SuppressLint("SetTextI18n")
    private fun extractBarcodeQrCodeInfo(barcodes: List<Barcode>) {

        for (barcode in barcodes){
            val bound = barcode.boundingBox
            val corners = barcode.cornerPoints

            val rawValue = barcode.rawValue
            Log.d(TAG,"extractBarcodeQrCodeInfo: rawValue: $rawValue")

            val valueType = barcode.valueType
            when(valueType){
                Barcode.TYPE_WIFI -> {
                    val typeWifi = barcode.wifi

                    val ssid = "${typeWifi?.ssid}"
                    val password = "${typeWifi?.password}"
                    var encryptionType = "${typeWifi?.encryptionType}"

                    if (encryptionType == "1") {
                        encryptionType = "OPEN"
                    } else if (encryptionType == "2") {
                        encryptionType = "WPA"
                    } else if (encryptionType == "3") {
                        encryptionType = "WEP"
                    }

                    Log.d(TAG,"extractBarcodeQrCodeInfo: TYPE_WIFI")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: ssid: $ssid")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: password: $password")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: encryptionType: $encryptionType")

                    resultTv.text = "TYPE_WIFI \nssid: $ssid \npassword: $password \nencryptionType: $encryptionType \n\nrawValue: $rawValue"
                }

                Barcode.TYPE_URL -> {
                    val typeUrl = barcode.url

                    val title = "${typeUrl?.title}"
                    val url = "${typeUrl?.url}"

                    Log.d(TAG,"extractBarcodeQrCodeInfo: TYPE_URL")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: title: $title")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: url: $url")

                    resultTv.text = "TYPE_URL \ntitle: $title \nurl: $url \n\nrawValue: $rawValue"
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        cameraButton = binding.cameraButton
//        galleryButton = binding.galleryButton
//        imageViewToScan = binding.imageViewToScan
//        scanButton = binding.scanButton
//        resultTv = binding.resultTv
//
//        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//        cameraButton.setOnClickListener {
//            if (checkCameraPermission()){
//                pickImageCamera()
//            } else {
//                requestCameraPermission()
//            }
//        }
//
//        galleryButton.setOnClickListener {
//            if (checkStoragePermission()){
//                pickImageGallery()
//            } else {
//                requestStoragePermission()
//            }
//        }
//
//        scanButton.setOnClickListener {
//
//        }
//    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data

            imageUri = data?.data
            Log.d(TAG, "galleryActivityResultLauncher: imageUri: $imageUri")

            imageViewToScan.setImageURI(imageUri)
        } else {
            showToast("Cancelled!")
        }
    }

    private fun pickImageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Image")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description")

        imageUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data

            Log.d(TAG, "cameraActivityResultLauncher: imageUri: $imageUri")

            imageViewToScan.setImageURI(imageUri)
        }
    }

    private fun checkStoragePermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestStoragePermission() {
        activity?.let {
            ActivityCompat.requestPermissions(it, storagePermissions, STORAGE_REQUEST_CODE)
        }
    }

    private fun checkCameraPermission(): Boolean{
        val resultCamera = (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        val resultStorage = (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

        return resultCamera && resultStorage
    }

    private fun requestCameraPermission() {
        activity?.let {
            ActivityCompat.requestPermissions(it, cameraPermissions, CAMERA_REQUEST_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {

                if (grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted){
                        pickImageCamera()
                    } else {
                        showToast("Camera & Storage Permission Are Required")
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (storageAccepted){
                        pickImageGallery()
                    } else {
                        showToast("Storage Permission is Required")
                    }
                }
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}