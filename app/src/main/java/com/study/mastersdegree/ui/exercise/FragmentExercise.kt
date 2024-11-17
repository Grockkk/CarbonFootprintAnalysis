package com.study.mastersdegree.ui.exercise

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.study.mastersdegree.R

class FragmentExercise : Fragment() {

    private var isRunning = false
    private var elapsedTime: Long = 0L
    private var startTime: Long = 0L

    private lateinit var timerText: TextView
    private lateinit var distanceText: TextView
    private lateinit var timeElapsedText: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonStop: Button

    private val handler = Handler(Looper.getMainLooper())
    private var distanceTravelled = 0.0
    private var lastLocation: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

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

        // Bind views
        timerText = root.findViewById(R.id.timer_text)
        distanceText = root.findViewById(R.id.distance_text)
        timeElapsedText = root.findViewById(R.id.time_elapsed)
        buttonStart = root.findViewById(R.id.button_start)
        buttonPause = root.findViewById(R.id.button_pause)
        buttonStop = root.findViewById(R.id.button_stop)

        // Set listeners
        buttonStart.setOnClickListener { startTimer() }
        buttonPause.setOnClickListener { pauseTimer() }
        buttonStop.setOnClickListener { stopTimer() }

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        initializeLocationTracking()

        return root
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
                    }
                    distanceText.text = "Distance: %.2f m".format(distanceTravelled)
                }
            }
        }
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

        // Show elapsed time and distance
        timeElapsedText.text = "Time Elapsed: ${formatTime(elapsedTime)}\nDistance: %.2f m".format(distanceTravelled)
        timeElapsedText.visibility = View.VISIBLE

        // Reset values
        elapsedTime = 0L
        distanceTravelled = 0.0
        timerText.text = "00:00:00"
        distanceText.text = "Distance: 0.0 m"
        buttonStart.visibility = View.VISIBLE
        buttonPause.visibility = View.GONE
        buttonStop.visibility = View.GONE
    }

    private fun formatTime(timeInMillis: Long): String {
        val seconds = (timeInMillis / 1000) % 60
        val minutes = (timeInMillis / (1000 * 60)) % 60
        val hours = (timeInMillis / (1000 * 60 * 60))
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
