package com.example.gamebreakglitch.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import java.util.Random

class GlitchCanvasView(context: Context) : View(context) {

    private val random = Random()
    private val linePaint = Paint().apply { isAntiAlias = false }
    private val artifactPaint = Paint().apply { style = Paint.Style.FILL }
    private val colorOverlayPaint = Paint().apply { style = Paint.Style.FILL }

    var intensity: String = "MEDIUM"
    var lcdLinesEnabled: Boolean = true
    var screenFlickerEnabled: Boolean = true
    var pixelCorruptionEnabled: Boolean = true
    var colorDistortionEnabled: Boolean = true

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        if (colorDistortionEnabled) {
            val alphaShift = when (intensity) {
                "MILD" -> random.nextInt(20)
                "MEDIUM" -> random.nextInt(45)
                else -> random.nextInt(80)
            }
            val tintColor = when (random.nextInt(4)) {
                0 -> Color.argb(alphaShift, 0, 255, 120)
                1 -> Color.argb(alphaShift, 255, 0, 200)
                2 -> Color.argb(alphaShift, 255, 230, 0)
                else -> Color.argb(alphaShift, 0, 180, 255)
            }
            colorOverlayPaint.color = tintColor
            canvas.drawRect(0f, 0f, w, h, colorOverlayPaint)
        }

        if (lcdLinesEnabled) {
            val lineCount = when (intensity) {
                "MILD" -> 15
                "MEDIUM" -> 40
                else -> 90
            }

            for (i in 0 until lineCount) {
                linePaint.color = Color.argb(
                    random.nextInt(180) + 70,
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256)
                )
                linePaint.strokeWidth = (random.nextInt(4) + 1).toFloat()

                if (random.nextBoolean()) {
                    val y = random.nextFloat() * h
                    canvas.drawLine(0f, y, w, y, linePaint)
                } else {
                    val x = random.nextFloat() * w
                    canvas.drawLine(x, 0f, x, h, linePaint)
                }
            }
        }

        if (pixelCorruptionEnabled) {
            val blockCount = when (intensity) {
                "MILD" -> 8
                "MEDIUM" -> 25
                else -> 60
            }

            for (i in 0 until blockCount) {
                artifactPaint.color = Color.argb(
                    random.nextInt(200) + 55,
                    if (random.nextBoolean()) 255 else 0,
                    if (random.nextBoolean()) 255 else 0,
                    if (random.nextBoolean()) 255 else 0
                )
                val left = random.nextFloat() * w
                val top = random.nextFloat() * h
                val right = left + random.nextInt(120) + 10
                val bottom = top + random.nextInt(40) + 5
                canvas.drawRect(left, top, right, bottom, artifactPaint)
            }
        }

        if (screenFlickerEnabled && random.nextInt(10) < 3) {
            val flickerPaint = Paint().apply {
                color = Color.argb(if (intensity == "EXTREME") 110 else 40, 255, 255, 255)
            }
            canvas.drawRect(0f, 0f, w, h, flickerPaint)
        }

        postInvalidateDelayed(if (intensity == "EXTREME") 30L else 70L)
    }
}
