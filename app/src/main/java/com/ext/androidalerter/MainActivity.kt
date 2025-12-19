package com.ext.androidalerter

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ext.android_alerter.Alerter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.view.View>(R.id.btnDefault).setOnClickListener {
            Alerter.create(this)
                .setTitle("Alert Title")
                .setText("Alert text...")
                .show()
        }

        findViewById<android.view.View>(R.id.btnColored).setOnClickListener {
            Alerter.create(this)
                .setTitle("Colored Alert")
                .setText("This has a custom background color")
                .setBackgroundColor(Color.parseColor("#4CAF50"))
                .show()
        }

        findViewById<android.view.View>(R.id.btnCustomIcon).setOnClickListener {
            Alerter.create(this)
                .setTitle("Custom Icon")
                .setText("Different icon and tint")
                .setIcon(R.drawable.alerter_ic_notifications)
                .setIconTint(Color.WHITE)
                .setIconSize(60)
                .setBackgroundColor(Color.BLUE)
                .show()
        }

        findViewById<android.view.View>(R.id.btnTextOnly).setOnClickListener {
            Alerter.create(this)
                .setTitle("Text Only")
                .setText("No icon here")
                .hideIcon()  // ← Correct way now
                .setBackgroundColor(Color.DKGRAY)
                .show()
        }

        findViewById<android.view.View>(R.id.btnOnClick).setOnClickListener {
            Alerter.create(this)
                .setTitle("Clickable Alert")
                .setText("Tap me!")
                .setBackgroundColor(Color.MAGENTA)
                .setOnClickListener {
                    Alerter.hide()
                }
                .show()
        }

        findViewById<android.view.View>(R.id.btnVerbose).setOnClickListener {
            Alerter.create(this)
                .setTitle("Verbose Alert")
                .setText("This is a longer message that demonstrates how the alert handles more text content gracefully.")
                .setDuration(5000)
                .show()
        }

        findViewById<android.view.View>(R.id.btnInfinite).setOnClickListener {
            Alerter.create(this)
                .setTitle("Infinite Alert")
                .setText("This won't dismiss automatically. Swipe up to dismiss.")
                .enableInfiniteDuration(true)
                .show()
        }

        findViewById<android.view.View>(R.id.btnProgress).setOnClickListener {
            Alerter.create(this)
                .setTitle("Progress Alert")
                .setText("This has a shrinking progress bar")
                .enableProgress(true)
                .setProgressColor(Color.BLACK)
                .setDuration(4000)
                .show()
        }
    }
}