package com.curiousdaya.scrollreel.liveness

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curiousdaya.scrollreel.R
import com.curiousdaya.scrollreel.databinding.ActivityDrawScreen2Binding
import com.curiousdaya.scrollreel.util.MyAccessibilityService

class DrawScreen2 : AppCompatActivity() , SensorEventListener {
    lateinit var binding: ActivityDrawScreen2Binding

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityDrawScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    /*    binding.btnClick.setOnClickListener {
            move()

        }*/


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            Toast.makeText(this, "Proximity sensor is not available!", Toast.LENGTH_SHORT).show()
            finish()  // Finish the activity or disable specific features if sensor is necessary.
        }


    }



    override fun onResume() {
        super.onResume()
        proximitySensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] < proximitySensor!!.maximumRange) {
                    // Detected something close
                    Log.d("Proximity", "Near")
                   // move()
                    Toast.makeText(this, "Near", Toast.LENGTH_SHORT).show()
                } else {
                    // Nothing is close
                    Log.d("Proximity", "Far")
                    Toast.makeText(this, "Far", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}
