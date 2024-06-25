package com.curiousdaya.scrollreel.liveness


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Outline
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.view.LifecycleCameraController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.curiousdaya.scrollreel.databinding.ActivityLivenessBinding
import com.curiousdaya.scrollreel.util.CameraLivenessService
import com.curiousdaya.scrollreel.util.MyAccessibilityService
import com.us47codex.liveness_detection.FaceAnalyzer
import com.us47codex.liveness_detection.LivenessDetector
import com.us47codex.liveness_detection.tasks.DetectionTask
import com.us47codex.liveness_detection.tasks.EyesBlinkDetectionTask


open class LivenessActivity : AppCompatActivity() , SensorEventListener {
    private lateinit var binding: ActivityLivenessBinding
    private lateinit var cameraController: LifecycleCameraController
     var livenessDetector = LivenessDetector(EyesBlinkDetectionTask(),)

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    companion object {
        const val CAMERA_REQUEST_CODE = 100
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLivenessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(1024,1024)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            Toast.makeText(this, "Please enable camera permision", Toast.LENGTH_SHORT).show()
        } else
        {
            val serviceIntent = Intent(this, CameraLivenessService::class.java)
            startForegroundService(serviceIntent)
            startCamera()
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)


        }
        binding.mACCESSIBILITY.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }



    }



    override fun onResume() {
        super.onResume()

        proximitySensor?.also { sensor ->
            sensorManager.registerListener(this@LivenessActivity, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun startCamera() {
        cameraController = LifecycleCameraController(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        setupAndStartLivenessDetection()
        cameraController.bindToLifecycle(this)
     //   binding.cameraPreview.controller = cameraController
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        startCamera()
    }

    private fun setupAndStartLivenessDetection() {
        val faceAnalyzer = FaceAnalyzer(buildLivenessDetector())
        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this), faceAnalyzer)
    }
    private fun move() {
        val intent = Intent(this, MyAccessibilityService::class.java).apply {
            putExtra("ACTION", "SWIPE")
            putExtra("FROM_X", 500f)
            putExtra("FROM_Y", 2000f)

            putExtra("TO_X", 500f)
            putExtra("TO_Y", 300f)
            putExtra("DURATION", 500L)
        }
        startService(intent)
    }

    private fun buildLivenessDetector(): LivenessDetector {
        val listener = object : LivenessDetector.Listener {
            @SuppressLint("SetTextI18n")
            override fun onTaskStarted(task: DetectionTask) {

            }
            override fun onTaskCompleted(task: DetectionTask, isLastTask: Boolean) {
                 move()
              /*  Toast.makeText(this@LivenessActivity, "You Blinked Eye Right Now Activity", Toast.LENGTH_SHORT).show()
                Log.d("DAYAAAAAAAAA", "You Blink your EYE: ACTIVITY")
*/
                /*  startActivity(Intent(this@LivenessActivity, LivenessActivity::class.java))
                  overridePendingTransition(
                     0,
                    0
                  )*/
            }

            override fun onTaskFailed(task: DetectionTask, code: Int) {
                val message = when (code) {
                    2 -> "Please make sure you are in a well-lit area and look directly at the camera."
                    0 -> "Error detected, trying again."
                    else -> "Unexpected error occurred."
                }
             //   Toast.makeText(this@LivenessActivity, message, Toast.LENGTH_LONG).show()
                // Consider retrying the task after a brief delay or adjusting settings
                if (code != 0) {
                    setupAndStartLivenessDetection() // Retry mechanism
                }
            }

        }


        return livenessDetector.also { it.setListener(listener) }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            if (distance < proximitySensor!!.maximumRange)
            {


            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


}