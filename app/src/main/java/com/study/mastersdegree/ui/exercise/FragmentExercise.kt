package com.study.mastersdegree.ui.exercise

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.study.mastersdegree.R

class FragmentExercise : Fragment() {

    private var isRunning = false
    private var elapsedTime: Long = 0L
    private var startTime: Long = 0L

    private lateinit var timerText: TextView
    private lateinit var timeElapsedText: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonStop: Button

    private val handler = Handler(Looper.getMainLooper())

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
        timeElapsedText = root.findViewById(R.id.time_elapsed)
        buttonStart = root.findViewById(R.id.button_start)
        buttonPause = root.findViewById(R.id.button_pause)
        buttonStop = root.findViewById(R.id.button_stop)

        // Set listeners
        buttonStart.setOnClickListener { startTimer() }
        buttonPause.setOnClickListener { pauseTimer() }
        buttonStop.setOnClickListener { stopTimer() }

        return root
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            startTime = System.currentTimeMillis()
            handler.post(updateTimerRunnable)

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

            buttonStart.visibility = View.VISIBLE
            buttonPause.visibility = View.GONE
        }
    }

    private fun stopTimer() {
        if (isRunning) {
            isRunning = false
            elapsedTime += System.currentTimeMillis() - startTime
            handler.removeCallbacks(updateTimerRunnable)
        }

        // Show elapsed time
        timeElapsedText.text = "Time Elapsed: ${formatTime(elapsedTime)}"
        timeElapsedText.visibility = View.VISIBLE

        // Reset values
        elapsedTime = 0L
        timerText.text = "00:00:00"
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
