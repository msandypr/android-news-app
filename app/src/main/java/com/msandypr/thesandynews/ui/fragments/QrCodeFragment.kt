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
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
            try {
                if (checkCameraPermission()) {
                    pickImageCamera()
                } else {
                    requestCameraAndStoragePermissions()
                }
            } catch (e: Exception) {
                Log.e(TAG, "cameraButton click failed", e)
                showToast("Error occurred while processing camera")

                FirebaseCrashlytics.getInstance().log("Error in cameraButton click: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        galleryButton.setOnClickListener {
            try {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryActivityResultLauncher.launch(galleryIntent)
            } catch (e: Exception) {
                Log.e(TAG, "galleryButton click failed", e)
                showToast("Error occurred while processing gallery")

                FirebaseCrashlytics.getInstance().log("Error in galleryButton click: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        scanButton.setOnClickListener {
            try {
                if (imageUri == null) {
                    showToast("Pilih gambarnya dulu sayang")
                } else {
                    detectResultFromImage()
                }
            } catch (e: Exception) {
                Log.e(TAG, "scanButton click failed", e)
                showToast("Error occurred while processing qr & barcode scan")

                FirebaseCrashlytics.getInstance().log("Error in scanButton click: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }



    private fun detectResultFromImage() {
        Log.d(TAG, "detectResultFromImage: ")
        try {
            barcodeScanner?.let { scanner ->
                barcodeScannerOptions?.let { options ->
                    val inputImage = InputImage.fromFilePath(requireContext(), imageUri!!)
                    val barcodeResult = scanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            extractBarcodeQrCodeInfo(barcodes)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "detectResultFromImage: Barcode scanning failed", e)
                            showToast("Failed scanning due to ${e.message}")

                            FirebaseCrashlytics.getInstance().log("Failed scanning QR code: ${e.message}")
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "detectResultFromImage: Exception", e)
            showToast("Failed due to ${e.message}")

            // Log error to Crashlytics
            FirebaseCrashlytics.getInstance().log("Failed processing image: ${e.message}")

            // Record exception
            FirebaseCrashlytics.getInstance().recordException(e)
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

                    resultTv.text = "Wi-Fi \nssid: $ssid \npassword: $password \nencryptionType: $encryptionType \n\nrawValue: $rawValue"
                }

                Barcode.TYPE_URL -> {
                    val typeUrl = barcode.url

                    val title = "${typeUrl?.title}"
                    val url = "${typeUrl?.url}"

                    Log.d(TAG,"extractBarcodeQrCodeInfo: TYPE_URL")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: title: $title")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: url: $url")

                    resultTv.text = "Internet URL \ntitle: $title \nurl: $url \n\nrawValue: $rawValue"
                }

                Barcode.TYPE_EMAIL -> {
                    val typeEmail = barcode.email

                    val address = "${typeEmail?.address}"
                    val body = "${typeEmail?.body}"
                    val subject = "${typeEmail?.subject}"

                    Log.d(TAG,"extractBarcodeQrCodeInfo: TYPE_EMAIL")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: title: $address")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: url: $body")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: url: $subject")

                    resultTv.text = "Email \naddress: $address \nbody: $body \nsubject: $subject \n\nrawValue: $rawValue"
                }

                Barcode.TYPE_CONTACT_INFO -> {
                    val typeContact = barcode.contactInfo

                    val title = "${typeContact?.title}"
                    val organization = "${typeContact?.organization}"
                    val name = "${typeContact?.name?.first} ${typeContact?.name?.last}"
                    val phone = "$${typeContact?.name?.first} ${typeContact?.phones?.get(0)?.number}"

                    Log.d(TAG,"extractBarcodeQrCodeInfo: TYPE_CONTACT_INFO")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: title: $title")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: organization: $organization")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: name: $name")
                    Log.d(TAG,"extractBarcodeQrCodeInfo: phone: $phone")

                    resultTv.text = "Contact Information \ntitle: $title \norganization: $organization \nname: $name \nphone: $phone \n\nrawValue: $rawValue"
                } else -> {
                    resultTv.text = "rawValue: $rawValue"
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

            // Log error to Crashlytics
            FirebaseCrashlytics.getInstance().log("Gallery picker cancelled.")
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
        } else {
            showToast("Cancelled!")

            // Log error to Crashlytics
            FirebaseCrashlytics.getInstance().log("Camera activity cancelled.")
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraAndStoragePermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!checkCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (!checkStoragePermission()) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    permissionsToRequest.toTypedArray(),
                    CAMERA_REQUEST_CODE
                )
            }
        } else {
            showToast("Camera & Storage Permissions are Already Granted")
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