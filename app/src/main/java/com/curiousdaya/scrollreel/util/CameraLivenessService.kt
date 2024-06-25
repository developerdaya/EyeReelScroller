package com.curiousdaya.scrollreel.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.curiousdaya.scrollreel.R
import com.curiousdaya.scrollreel.liveness.MainActivity
import com.us47codex.liveness_detection.FaceAnalyzer
import com.us47codex.liveness_detection.LivenessDetector
import com.us47codex.liveness_detection.tasks.DetectionTask
import com.us47codex.liveness_detection.tasks.EyesBlinkDetectionTask


class CameraLivenessService : LifecycleService() {
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector
    var livenessDetector = LivenessDetector(EyesBlinkDetectionTask())


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startForegroundServiceWithNotification()
        initializeCameraAndDetection()
        Log.d("DAYAAAAAAAAA", "Service Started")
        Toast.makeText(applicationContext, "Eye Scanning Completed", Toast.LENGTH_LONG).show()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundServiceWithNotification() {
        createNotificationChannel()
        val notification = createNotification()
        startForeground(1, notification)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            1.toString(), "Liveliness Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        Log.d("DAYAAAAAAAAA", "createNotificationChannel")
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, "1")
            .setContentTitle("Camera and Liveness Detection")
            .setContentText("The service is running in the background.")
            .setSmallIcon(R.drawable.ic_camera_switch)
            .setContentIntent(pendingIntent)
            .build()
        Log.d("DAYAAAAAAAAA", "createNotification")

    }

    private fun initializeCameraAndDetection() {
        val cameraFuture = ProcessCameraProvider.getInstance(this)
        cameraFuture.addListener({
            cameraProvider = cameraFuture.get()
            setupCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
        Log.d("DAYAAAAAAAAA", "initializeCameraAndDetection")

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

    private fun setupCameraUseCases() {
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        Log.d("DAYAAAAAAAAA", "DEFAULT_FRONT_CAMERA START")

        val preview = Preview.Builder().build()
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(this), FaceAnalyzer(buildLivenessDetector2()))
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        } catch (e: Exception) {
            Log.e(TAG, "Camera bind failed", e)
        }

        Log.d("DAYAAAAAAAAA", "setupCameraUseCases")

    }


    private fun buildLivenessDetector2(): LivenessDetector {
        val listener = object : LivenessDetector.Listener {
            @SuppressLint("SetTextI18n")
            override fun onTaskStarted(task: DetectionTask) {

            }
            override fun onTaskCompleted(task: DetectionTask, isLastTask: Boolean) {
                Log.d("DAYAAAAAAAAA", "You Blink your EYE: SERVICES")
                move()
               // Toast.makeText(applicationContext, "You Blink your EYE: SERVICES", Toast.LENGTH_LONG).show()
                 livenessDetector = LivenessDetector(EyesBlinkDetectionTask())
                 initializeCameraAndDetection()



            }

            override fun onTaskFailed(task: DetectionTask, code: Int) {
                val message = when (code) {
                    2 -> "Please make sure you are in a well-lit area and look directly at the camera."
                    0 -> "Error detected, trying again."
                    else -> "Unexpected error occurred."
                }
               // Toast.makeText(applicationContext, "$message", Toast.LENGTH_LONG).show()

            }

        }


        return livenessDetector.also { it.setListener(listener) }
    }






}
