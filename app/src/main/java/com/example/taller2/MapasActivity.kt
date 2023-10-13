package com.example.taller2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.taller2.databinding.ActivityMapas2Binding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MapStyleOptions
import java.io.IOException


class MapasActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapas2Binding
    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var lightSensorListener: SensorEventListener
    private lateinit var locationManager: LocationManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var latitud = 0.0
    var longitud = 0.0
    var lux = 0.0
    private val handler = Handler()
    private val updateInterval = 2000L // Intervalo de actualización en milisegundos
    private lateinit var editTextAddress: EditText
    private lateinit var geocoder: Geocoder
    private var longClickLocation: Location? = null
    private var searchLocationMarkerLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapas2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        editTextAddress = findViewById(R.id.searchLocation)
        geocoder = Geocoder(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()


        //Sensor de luz
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        startMapUpdateLoop()

        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                lux = event.values[0].toDouble()
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
        }

        editTextAddress.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                searchLocationPutMarker()
                return@OnEditorActionListener true
            }
            false
        })


    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            lightSensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL

        )
        setLocation()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightSensorListener)
    }


    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MapasActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MapasActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

            }
            ActivityCompat.requestPermissions(
                this@MapasActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else if (ActivityCompat.checkSelfPermission(
                this@MapasActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MapasActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation()
                }
                return
            }

            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation()
                }
                return
            }

            else -> {
                //algo mas
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        updateMapStyle()

        if (mMap != null)
            if (lux < 5000) {
                Log.i(" MAPS", "DARK MAP " + lux)
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this@MapasActivity,
                        R.raw.style_json2
                    )
                )
            } else {
                Log.i(" MAPS", "LIGHT MAP " + lux)
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this@MapasActivity,
                        R.raw.style_json
                    )
                )
            }

        //setear la localización
        if (ActivityCompat.checkSelfPermission(
                this@MapasActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MapasActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                Log.i("LOCATION", "onSucess location")
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                    Log.i("miLatitud", latitud.toString())
                    Log.i("miLongitud", longitud.toString())
                    //Poner un marcador en la ubicación del usuario
                    var miUbicacion = LatLng(latitud, longitud)
                    Log.i("Mi ubicación", miUbicacion.toString())
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
                    mMap.addMarker(MarkerOptions().position(miUbicacion).title("Mi ubicación"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion))
                }

            }

        }


        // Agrega un listener para el evento "Long Click" en el mapa
        mMap.setOnMapLongClickListener { latLng ->
            // Convierte las coordenadas de latitud y longitud en una dirección usando Geocoder
            val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    // Obtiene la dirección de la lista
                    val address = addressList.get(0)?.getAddressLine(0)

                    // Agrega un marcador con la posición y el título de la dirección
                    mMap.addMarker(MarkerOptions().position(latLng).title(address))
                } else {
                    // Si no se encontró ninguna dirección, agrega un marcador sin título
                    mMap.addMarker(MarkerOptions().position(latLng))
                }
            }
        }



        mMap.uiSettings.isZoomControlsEnabled = true // Habilitar los "gestures" como "pinch to zoom"
        mMap.uiSettings.isZoomGesturesEnabled = true // Habilitar los botones de zoom
    }

    private fun setLocation() {
        //setear la localización
        if (ActivityCompat.checkSelfPermission(
                this@MapasActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MapasActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                Log.i("LOCATION", "onSucess location")
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                    Log.i("miLatitud", latitud.toString())
                    Log.i("miLongitud", longitud.toString())
                }
            }
        }
    }

    private fun startMapUpdateLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateMapStyle()
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }


    private fun updateMapStyle() {
        if (mMap != null) {
            if (lux < 5000) {
                Log.i("MAPS", "DARK MAP " + lux)
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this@MapasActivity,
                        R.raw.style_json2
                    )
                )
            } else {
                Log.i("MAPS", "LIGHT MAP " + lux)
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this@MapasActivity,
                        R.raw.style_json
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener el bucle de actualización al salir de la actividad
        handler.removeCallbacksAndMessages(null)
    }

    private fun searchLocationPutMarker() {
        val address = binding.searchLocation.text.toString()
        if (address.isNotEmpty()) {
            try {
                val addressList = geocoder.getFromLocationName(address, 2)
                if (addressList!!.isNotEmpty()) {
                    val location = addressList[0]
                    var newLocation = LatLng(location.latitude, location.longitude)
                    if (mMap != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15.0f))
                        mMap.addMarker(MarkerOptions().position(newLocation).title(address))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
                    } else {
                        Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Ingrese una dirección", Toast.LENGTH_SHORT).show()
        }
    }

}