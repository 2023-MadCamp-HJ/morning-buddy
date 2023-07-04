package com.example.madcampproj1.tab3

import android.media.RingtoneManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.madcampproj1.R

// 푸시알림 -> 알람해제 가능
class DismissAlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dismiss_alarm)

        // 알람 소리를 끕니다.
        MyBroadcastReceiver.ringtone?.stop()

        // 앱이 종료됩니다.
        finish()
    }
}


//class DismissAlarmActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dismiss_alarm)
//
//        // 알람 소리를 끕니다.
//        RingtoneManager.stopPreviousRingtone()
//
//        // 알람 해제 버튼을 누르면 앱이 종료됩니다.
//        finish()
//    }
//}
