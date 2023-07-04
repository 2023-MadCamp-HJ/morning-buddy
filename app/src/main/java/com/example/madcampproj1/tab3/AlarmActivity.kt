package com.example.madcampproj1.tab3

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.madcampproj1.R
import org.json.JSONArray
class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val dismissButton = findViewById<Button>(R.id.dismissButton)
        dismissButton.setOnClickListener {
            // 알람 소리를 끕니다.
            MyBroadcastReceiver.ringtone?.stop()

            // 해당 알람의 isEnabled 속성을 false로 설정합니다.
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            if (alarmId != -1) {
                val sharedPreferences = getSharedPreferences("alarms", Context.MODE_PRIVATE)
                val alarmsJson = sharedPreferences.getString("alarms", "[]")
                val alarmsArray = JSONArray(alarmsJson)
                for (i in 0 until alarmsArray.length()) {
                    val alarmJson = alarmsArray.getJSONObject(i)
                    if (alarmJson.getInt("id") == alarmId) {
                        alarmJson.put("isEnabled", false)
                        break
                    }
                }
                sharedPreferences.edit().putString("alarms", alarmsArray.toString()).apply()
            }

            // 앱이 종료됩니다.
            finish()
        }
    }
}

//class AlarmActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_alarm)
//
//        val dismissButton = findViewById<Button>(R.id.dismissButton)
//        dismissButton.setOnClickListener {
//            // 알람 소리를 끕니다.
//            MyBroadcastReceiver.ringtone?.stop()
//
//            // 앱이 종료됩니다.
//            finish()
//        }
//    }
//}
//
