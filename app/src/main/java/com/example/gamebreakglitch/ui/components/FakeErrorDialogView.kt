package com.example.gamebreakglitch.ui.components

import android.content.Context
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class FakeErrorDialogView(context: Context, private val onCloseRequested: () -> Unit) : LinearLayout(context) {

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#1E1E2C"))
        setPadding(60, 50, 60, 50)

        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(50, 0, 50, 0)
        }
        layoutParams = params

        val title = TextView(context).apply {
            text = getFakeTitle()
            setTextColor(Color.parseColor("#FF5555"))
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val message = TextView(context).apply {
            text = "The GPU rendering pipeline encountered an unexpected sync timeout [Code 0x800705B4]. Display calibration failed. Tap recover to re-initialize driver."
            setTextColor(Color.WHITE)
            textSize = 14f
            setPadding(0, 20, 0, 30)
        }

        val btn = Button(context).apply {
            text = "Wait / Recover"
            setBackgroundColor(Color.parseColor("#3B3B58"))
            setTextColor(Color.WHITE)
            setOnClickListener { onCloseRequested() }
        }

        addView(title)
        addView(message)
        addView(btn)
    }

    private fun getFakeTitle(): String {
        val titles = listOf(
            "Display Driver Not Responding",
            "GPU Rendering Error",
            "Screen Calibration Failed",
            "Display Hardware Timeout",
            "Graphic Subsystem Restarting"
        )
        return titles.random()
    }
}
