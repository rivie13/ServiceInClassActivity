package edu.temple.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var timerBinder: TimerService.TimerBinder? = null
    private var isBound = false
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            timerBinder = service as TimerService.TimerBinder
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            timerBinder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        val serviceIntent = Intent(this, TimerService::class.java)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

        startButton.setOnClickListener {
            if (isBound) {
                if (!timerBinder!!.isRunning) {
                    timerBinder?.start(5) // Start with 10 seconds
                    startButton.text = "Pause"
                } else if (timerBinder!!.paused) {
                    timerBinder?.pause()
                    startButton.text = "Pause"
                } else {
                    timerBinder?.pause()
                    startButton.text = "Resume"
                }
            }
        }

        stopButton.setOnClickListener {
            if (isBound) {
                timerBinder?.stop()
                startButton.text = "Start"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}