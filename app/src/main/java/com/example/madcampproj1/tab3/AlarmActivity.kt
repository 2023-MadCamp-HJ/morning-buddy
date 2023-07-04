package com.example.madcampproj1.tab3

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.madcampproj1.R

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val dismissButton = findViewById<Button>(R.id.dismissButton)
        dismissButton.setOnClickListener {
            // 알람 소리를 끕니다.
            MyBroadcastReceiver.ringtone?.stop()

            // 앱이 종료됩니다.
            finish()
        }
    }
}

