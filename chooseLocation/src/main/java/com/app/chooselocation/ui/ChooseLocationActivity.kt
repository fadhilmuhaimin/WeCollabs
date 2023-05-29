package com.app.chooselocation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.chooselocation.R
import com.app.chooselocation.databinding.ActivityChooseLocationBinding
import com.app.core.data.LATITUDE
import com.app.core.data.LONGITUDE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ChooseLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityChooseLocationBinding
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initListener() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.mapPick.submitBtn.setOnClickListener {
            if(latitude != null && longitude != null) {
                val intent = Intent()
                intent.putExtra(LATITUDE, latitude)
                intent.putExtra(LONGITUDE, longitude)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_location), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val defaultLocation = LatLng(-5.148679046531416, 119.4324808936543)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        mMap.setOnMapClickListener {
            latitude = it.latitude
            longitude = it.longitude
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(it).title(getString(com.app.wecollabs.R.string.location)))
            binding.mapPick.mapLatLng.text = StringBuilder("${it.latitude.toFloat()}").append(", ${it.longitude.toFloat()}")
        }
    }
}