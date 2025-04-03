package com.study.mastersdegree.ui.exercise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.*
import com.study.mastersdegree.R
import com.study.mastersdegree.helpers.EmissionCalculator
import com.study.mastersdegree.ui.shared.SharedViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class FragmentExercise : Fragment() {

    // Timer variables
    private var isRunning = false
    private var elapsedTime: Long = 0L
    private var startTime: Long = 0L

    // UI elements
    private lateinit var timerText: TextView
    private lateinit var distanceText: TextView
    private lateinit var emissionText: TextView
    private lateinit var costText: TextView
    private lateinit var buttonStart: ImageButton
    private lateinit var buttonPause: ImageButton
    private lateinit var buttonStop: ImageButton
    private lateinit var historyLayout: LinearLayout
    private lateinit var histEmissionText: TextView
    private lateinit var histDistanceText: TextView
    private lateinit var histCostText: TextView


    // Location variables
    private val handler = Handler(Looper.getMainLooper())
    private var distanceTravelled = 0.0
    private var lastLocation: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mapView: MapView

    // Emission and cost calculator
    private lateinit var emissionCalculator: EmissionCalculator

    // Dynamic values for fuel type and price
    private var fuelType = "Benzyna" // Default fuel type
    private var fuelPricePerLiter = 6.0 // Default fuel price in PLN
    private var fuelConsumption = 6.0 // Default fuel price in PLN
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentTime = System.currentTimeMillis()
                val totalElapsed = currentTime - startTime + elapsedTime
                timerText.text = formatTime(totalElapsed)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_exercise, container, false)

        // Initialize map
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        mapView = root.findViewById(R.id.mapview)
        initializeMap()

        // Initialize UI elements
        historyLayout = root.findViewById(R.id.history_layout)
        timerText = root.findViewById(R.id.timer_text)
        distanceText = root.findViewById(R.id.distance_text)
        emissionText = root.findViewById(R.id.emission_text)
        costText = root.findViewById(R.id.cost_text)
        buttonStart = root.findViewById(R.id.button_start)
        buttonPause = root.findViewById(R.id.button_pause)
        buttonStop = root.findViewById(R.id.button_stop)
        histDistanceText = root.findViewById(R.id.hist_dyst_text)
        histCostText = root.findViewById(R.id.hist_cost_text)
        histEmissionText = root.findViewById(R.id.hist_emission_text)


        // Initialize listeners
        buttonStart.setOnClickListener { startTimer() }
        buttonPause.setOnClickListener { pauseTimer() }
        buttonStop.setOnClickListener { stopTimer() }

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        initializeLocationTracking()

        // Initialize emission calculator
        emissionCalculator = EmissionCalculator()

        return root
    }

    private fun initializeMap() {
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(50.064535606764245, 19.92365699452155)
        mapController.setCenter(startPoint)

        // Add a marker for the start location
        val startMarker = Marker(mapView)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Start Location"
        mapView.overlays.add(startMarker)
    }

    private fun initializeLocationTracking() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isRunning) {
                    for (location in locationResult.locations) {
                        if (lastLocation != null) {
                            distanceTravelled += lastLocation!!.distanceTo(location)
                        }
                        lastLocation = location
                        // Update the map with the current location
                        updateMapLocation(location)
                    }
                    val distanceInKm = distanceTravelled / 1000
                    distanceText.text = "%.2f km".format(distanceInKm)


                    val (totalEmissions, totalCost) = sharedViewModel.calculateEmissionAndCostForDistance(distanceInKm)

                    emissionText.text = "%.2f kg".format(totalEmissions)
                    costText.text = "%.2f PLN".format(totalCost)
                }
            }
        }
    }

    private fun updateMapLocation(location: Location) {
        val currentGeoPoint = GeoPoint(location.latitude, location.longitude)
        val mapController = mapView.controller
        mapController.setCenter(currentGeoPoint)

        // Create or update the marker to show the current location
        val marker = Marker(mapView)
        marker.position = currentGeoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Your Location"

        // Clear previous markers and add the new one
        mapView.overlays.clear()
        mapView.overlays.add(marker)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 2000 // Update every 2 seconds
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        lastLocation = null
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            startTime = System.currentTimeMillis()
            handler.post(updateTimerRunnable)
            startLocationUpdates()

            buttonStart.visibility = View.GONE
            buttonPause.visibility = View.VISIBLE
            buttonStop.visibility = View.VISIBLE
        }
    }

    private fun pauseTimer() {
        if (isRunning) {
            isRunning = false
            elapsedTime += System.currentTimeMillis() - startTime
            handler.removeCallbacks(updateTimerRunnable)
            stopLocationUpdates()


            buttonStart.visibility = View.VISIBLE
            buttonPause.visibility = View.GONE
        }
    }

    private fun stopTimer() {
        if (isRunning) {
            isRunning = false
            elapsedTime += System.currentTimeMillis() - startTime
            handler.removeCallbacks(updateTimerRunnable)
            stopLocationUpdates()
        }

        // Pobierz wartości do wyświetlenia
        val distanceValue = "%.2f km".format(distanceTravelled / 1000)
        val emissionValue = emissionText.text.toString()
        val costValue = costText.text.toString()

        // Dodaj wyniki do historii
        addRunToHistory(distanceValue, emissionValue, costValue)

        // Resetuj UI
        timerText.text = "00:00:00"
        distanceText.text = "0,00 km"
        emissionText.text = "0,00 kg"
        costText.text = "0,00 PLN"
        distanceTravelled = 0.00
        elapsedTime = 0L

        buttonStart.visibility = View.VISIBLE
        buttonPause.visibility = View.GONE
        buttonStop.visibility = View.GONE
    }

    private fun formatTime(timeInMillis: Long): String {
        val seconds = timeInMillis / 1000 % 60
        val minutes = timeInMillis / 60000 % 60
        val hours = timeInMillis / 3600000
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    private fun addRunToHistory(distance: String, emission: String, cost: String) {
        historyLayout.visibility = View.VISIBLE

        histEmissionText.text = "$emission"
        histDistanceText.text = "$distance"
        histCostText.text = "$cost"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
        handler.removeCallbacks(updateTimerRunnable)
    }
}
