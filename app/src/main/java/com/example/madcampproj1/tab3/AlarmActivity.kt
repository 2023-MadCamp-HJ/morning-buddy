package com.example.madcampproj1.tab3

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import com.example.madcampproj1.R
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class AlarmActivity : AppCompatActivity() {
    private var isDismissed = false
    private var isTemporaryExit = false
    private var alarmCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        // alarmCount 초기화
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        if (alarmId != -1) {
            val sharedPreferences = getSharedPreferences("alarms", Context.MODE_PRIVATE)
            alarmCount = sharedPreferences.getInt("alarmCount$alarmId", 0)
        }

        // 현재 시각 표시
        val timeText = findViewById<TextView>(R.id.timeText)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeText.text = currentTime

        val dismissButton = findViewById<Button>(R.id.dismissButton)
        dismissButton.setOnClickListener {
            // 알람 소리를 끕니다.
            MyBroadcastReceiver.ringtone?.stop()

            // 해당 알람의 isEnabled 속성을 false로 설정합니다.
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
        }, 15 * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isDismissed && isTemporaryExit) {
            // 알람 소리를 끕니다.
            MyBroadcastReceiver.ringtone?.stop()

            // 알람이 임시 종료되었을 경우 알람을 다시 활성화합니다.
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            if (alarmId != -1) {
                if (alarmCount < 2) {
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

                    alarmCount++

                    // alarmCount 저장
                    sharedPreferences.edit().putInt("alarmCount$alarmId", alarmCount).apply()
                } else {
                    // 해당 알람의 isEnabled 속성을 false로 설정합니다.
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

                    // 연락처 앱과 연동하여 가장 첫번째 사람에게 문자 메시지를 보냅니다.
                    val cursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val columnIndex =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        if (columnIndex != -1) {
                            val phoneNumber = cursor.getString(columnIndex)
                            println("########PHONENUMBER"+ phoneNumber)
                            cursor.close()

                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:$phoneNumber")
                                putExtra("sms_body", "모닝콜 부탁해요!")
                            }
                            startActivity(smsIntent)
                        } else {
                            cursor.close()
                        }
                    }
                }
            }
        }
    }
}
