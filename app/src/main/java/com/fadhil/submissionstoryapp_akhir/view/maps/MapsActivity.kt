package com.fadhil.submissionstoryapp_akhir.view.maps

import android.content.ContentValues
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.fadhil.submissionstoryapp_akhir.R
import com.fadhil.submissionstoryapp_akhir.databinding.ActivityMapsBinding
import com.fadhil.submissionstoryapp_akhir.pref.ViewModelFactory

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLngBounds
import java.util.concurrent.TimeUnit

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var boundsBuilder = LatLngBounds.Builder()

    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val mapViewModel : MapsViewModel by viewModels{
        factory
    }

    private fun getData(){
        mapViewModel.getStoryList().observe(this){
            it.forEach { storyEntity ->
                val latLng = LatLng(storyEntity.lat?:0.0, storyEntity.lon?:0.0)
                mMap.addMarker(MarkerOptions().position(latLng).title(storyEntity.name))
                boundsBuilder.include(latLng)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds, 15
                )
            )
        }
    }

    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){ permit ->
            when {
                permit[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permit[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Toast.makeText(this, getString(R.string.no_permission_location), Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        when (it.resultCode) {
            RESULT_OK -> Log.i(ContentValues.TAG, "onActivityResult: All location settings are satisfied.")
            RESULT_CANCELED ->
                Toast.makeText(this@MapsActivity, getString(R.string.GPS_off), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyLastLocation() {
        if(checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null){
                    showCurrentLocation(it)
                } else {
                    Toast.makeText(this, "Location is not found. Try Again.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermission.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showCurrentLocation(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(currentLocation)
                .title(getString(R.string.curr_location))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener {
                if (it is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(it.resolution).build()
                        )
                    } catch (intEx: IntentSender.SendIntentException) {
                        Toast.makeText(this@MapsActivity, intEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        getMyLastLocation()
        createLocationRequest()
        getData()
        //setMapStyle()
    }
}