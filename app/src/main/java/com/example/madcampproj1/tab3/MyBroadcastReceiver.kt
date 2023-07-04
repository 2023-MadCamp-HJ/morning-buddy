package com.example.madcampproj1.tab3

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.madcampproj1.R
import java.util.*

class MyBroadcastReceiver : BroadcastReceiver() {

    companion object {
        var ringtone: Ringtone? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone?.play()

        // 알람 화면을 표시합니다.
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ALARM_ID", intent.getIntExtra("ALARM_ID", -1))
        }
        context.startActivity(alarmIntent)
    }
}


//class MyBroadcastReceiver : BroadcastReceiver() {
//
//    companion object {
//        var ringtone: Ringtone? = null
//    }
//
//    override fun onReceive(context: Context, intent: Intent) {
//        // 알람 울릴 때 실행할 작업을 여기에 작성합니다.
//        // 예를 들어, 다음과 같이 알람을 울리도록 할 수 있습니다.
//        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        ringtone = RingtoneManager.getRingtone(context, uri)
//        ringtone?.play()
//
//        // 알람 화면을 표시합니다.
//        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        context.startActivity(alarmIntent)
//    }
//}
