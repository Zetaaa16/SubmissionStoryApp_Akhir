package com.fadhil.submissionstoryapp_akhir.view.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fadhil.submissionstoryapp_akhir.R
import com.fadhil.submissionstoryapp_akhir.databinding.ActivityAddStoryBinding
import com.fadhil.submissionstoryapp_akhir.pref.Utils.reduceFileSize
import com.fadhil.submissionstoryapp_akhir.pref.Utils.rotateBitmap
import com.fadhil.submissionstoryapp_akhir.pref.Utils.uriToFile
import com.fadhil.submissionstoryapp_akhir.pref.ViewModelFactory
import com.fadhil.submissionstoryapp_akhir.view.camera.CameraActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.fadhil.submissionstoryapp_akhir.remote.Result
import com.fadhil.submissionstoryapp_akhir.view.login.LoginActivity
import com.fadhil.submissionstoryapp_akhir.view.main.MainActivity
import com.fadhil.submissionstoryapp_akhir.view.maps.MapsActivity

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val addStoryViewModel: AddStoryViewModel by viewModels()
    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val uploadViewModel : AddStoryViewModel by viewModels{
            factory
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUESTED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            //edttext.buttonSwitch()

            btnOpenCamera.setOnClickListener {
                val intent = Intent(this@AddStoryActivity, CameraActivity::class.java)
                launcherIntentCamera.launch(intent)
            }

            btnOpenImage.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, getString(R.string.choose_img))
                launcherIntentGallery.launch(chooser)
            }

            btnUpload.setOnClickListener {
                manifestLoading(true)
                if (getFile != null) {
                    val file = reduceFileSize(getFile as File)

                    val desc = "${edttext.text}".toRequestBody("text/plain".toMediaType())
                    val reqImgFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imgMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        reqImgFile
                    )
                    val lat: RequestBody?
                    val lon: RequestBody?
                    if (location != null){
                        lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                        lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                    } else{
                        Toast.makeText(this@AddStoryActivity, resources.getString(R.string.no_location), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    uploadViewModel.sendStory(imgMultiPart, desc, lat, lon)
                    uploadViewModel.addStory().observe(this@AddStoryActivity){
                        when (it) {
                            is Result.Loading -> {
                                binding.progressbar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressbar.visibility = View.GONE
                                Toast.makeText(
                                    this@AddStoryActivity,
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Intent(this@AddStoryActivity, MainActivity::class.java).run {
                                    startActivity(this)
                                }
                            }
                            is Result.Error -> {
                                binding.progressbar.visibility = View.GONE
                                Toast.makeText(this@AddStoryActivity, it.error, Toast.LENGTH_SHORT)
                                    .show()

                                Toast.makeText(this@AddStoryActivity,"error", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        resources.getString(R.string.no_file_yet),
                        Toast.LENGTH_SHORT
                    ).show()
                    manifestLoading(false)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_permission_camera),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }



    private fun allPermissionsGranted() = REQUESTED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("photo") as File
            val isBackCam = it.data?.getBooleanExtra("isBackCam", true) as Boolean
            getFile = myFile
            val result =
                rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCam)

            binding.imgPreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val img: Uri = result.data?.data as Uri
            val file = uriToFile(img, this@AddStoryActivity)
            getFile = file
            binding.imgPreview.setImageURI(img)
        }
    }



    private fun manifestLoading(state: Boolean){
        if (state) {
            binding.progressbar.visibility = View.VISIBLE
            binding.imgPreview.visibility = View.INVISIBLE
        } else{
            binding.progressbar.visibility = View.GONE
            binding.imgPreview.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        getLocation()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun getLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    this.location = loc
                } else {
                    Toast.makeText(
                        this, getString(R.string.GPS_off), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 0

        private val REQUESTED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}