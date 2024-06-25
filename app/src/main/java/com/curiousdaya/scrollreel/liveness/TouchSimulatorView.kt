package com.curiousdaya.scrollreel.liveness

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class TouchSimulatorView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private var currentY = 0f

    init {
        setWillNotDraw(false)
    }

    fun simulateTouch(yStart: Float, yEnd: Float, duration: Long) {
        val animator = ValueAnimator.ofFloat(yStart, yEnd).apply {
            this.duration = duration
            addUpdateListener { animation ->
                currentY = animation.animatedValue as Float
                invalidate()
            }
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        if (canvas != null) {
            super.onDraw(canvas)
        }
        // Draw a circle at the currentY position to represent the touch
        canvas?.drawCircle(width / 2f, currentY, 50f, paint)
    }
}
