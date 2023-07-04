package com.example.madcampproj1.tab3

import android.Manifest.permission.SEND_SMS
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import com.example.madcampproj1.R
import org.json.JSONArray
import java.util.*

class AlarmActivity : AppCompatActivity() {
    private var isDismissed = false
    private var isTemporaryExit = false

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

            // 앱이 임시 종료됩니다.
            isDismissed = true
            isTemporaryExit = true
            finish()
        }

        // 30초 후에 코드 실행 (알람 지속 시간 30초)
        val handler = HandlerCompat.createAsync(Looper.getMainLooper())
        handler.postDelayed({
            if (!isDismissed) {
                // 알람 해제되지 않음
                // 앱 임시 종료
                isTemporaryExit = true
                finish()
            }
        }, 30 * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isDismissed && isTemporaryExit) {
            // 알람 소리를 끕니다.
            MyBroadcastReceiver.ringtone?.stop()

            // 알람이 임시 종료되었을 경우 알람을 다시 활성화합니다.
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            if (alarmId != -1) {
                val sharedPreferences = getSharedPreferences("alarms", Context.MODE_PRIVATE)
                val alarmsJson = sharedPreferences.getString("alarms", "[]")
                val alarmsArray = JSONArray(alarmsJson)
                for (i in 0 until alarmsArray.length()) {
                    val alarmJson = alarmsArray.getJSONObject(i)
                    if (alarmJson.getInt("id") == alarmId) {
                        alarmJson.put("isEnabled", true)
                        break
                    }
                }
                sharedPreferences.edit().putString("alarms", alarmsArray.toString()).apply()

                // 1분 후에 알람이 다시 울리도록 설정합니다.
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 1)
                }
                val intent = Intent(this, MyBroadcastReceiver::class.java).apply {
                    putExtra("ALARM_ID", alarmId)
                }
                val pendingIntent =
                    PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val alarmManager =
                    getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
    }
}
