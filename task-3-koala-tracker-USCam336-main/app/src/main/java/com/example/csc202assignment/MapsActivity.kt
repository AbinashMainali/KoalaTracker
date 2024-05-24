package com.example.csc202assignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.csc202assignment.databinding.ActivityMapsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

      updateUI()
    }

    private fun updateUI(){
        val myPoint = LatLng(-22.7174, 153.06251)
        val myMarker: MarkerOptions = MarkerOptions().position(myPoint).title("Receipt Location")

        mMap.clear()
        mMap.addMarker(myMarker)

        val zoomLevel: Float = 15f
        val update: CameraUpdate = CameraUpdateFactory.newLatLngZoom(myPoint, zoomLevel)

        mMap.animateCamera(update)
    }


}