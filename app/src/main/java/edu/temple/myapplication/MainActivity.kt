package edu.temple.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var timerBinder: TimerService.TimerBinder? = null
    private var isBound = false
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var timerTextView: TextView

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            timerTextView.text = msg.what.toString()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder?.setHandler(handler)
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
        timerTextView = findViewById(R.id.textView)

        val serviceIntent = Intent(this, TimerService::class.java)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

        startButton.setOnClickListener {
            if (isBound) {
                if (!timerBinder!!.isRunning) {
                    timerBinder?.start(10) // Start with 10 seconds
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
                timerTextView.text = "0"
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