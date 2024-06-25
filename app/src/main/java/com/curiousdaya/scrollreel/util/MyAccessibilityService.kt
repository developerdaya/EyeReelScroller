package com.curiousdaya.scrollreel.util
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        {
            if (event.packageName == "com.example.someapp" && event.className == "com.example.someapp.MainActivity") {
                // Perform a swipe from (100, 300) to (300, 300) over 500 milliseconds
                swipe(100f, 300f, 300f, 300f, 500)
            }
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("ACTION")
        if (action == "SWIPE") {
            // Extract coordinates and duration from the intent
            val fromX = intent.getFloatExtra("FROM_X", 0f)
            val fromY = intent.getFloatExtra("FROM_Y", 0f)
            val toX = intent.getFloatExtra("TO_X", 0f)
            val toY = intent.getFloatExtra("TO_Y", 0f)
            val duration = intent.getLongExtra("DURATION", 500)
            swipe(fromX, fromY, toX, toY, duration)
        }
        return START_NOT_STICKY
    }

    override fun onInterrupt() {
        // Called if the service is about to be interrupted.
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Configure service here if needed
    }


    fun swipe(fromX: Float, fromY: Float, toX: Float, toY: Float, duration: Long) {
        val path = Path().apply {
            moveTo(fromX, fromY)
            lineTo(toX, toY)
        }
        val gestureBuilder = GestureDescription.Builder()
        val stroke = GestureDescription.StrokeDescription(path, 0, duration)
        gestureBuilder.addStroke(stroke)
        dispatchGesture(gestureBuilder.build(), null, null)
    }
}
