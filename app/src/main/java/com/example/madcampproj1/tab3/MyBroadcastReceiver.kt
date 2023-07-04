package com.example.madcampproj1.tab3

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.madcampproj1.R
import java.util.*

class MyBroadcastReceiver : BroadcastReceiver() {

    companion object {
        var ringtone: Ringtone? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        // 알람 울릴 때 실행할 작업을 여기에 작성합니다.
        // 예를 들어, 다음과 같이 알람을 울리도록 할 수 있습니다.
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone?.play()

        // 알람 화면을 표시합니다.
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(alarmIntent)
    }

}


// 푸시알림 -> 알람해제 가능
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
//        // 알람 시간을 표시합니다.
//        val calendar = Calendar.getInstance()
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//        val timeText = String.format("%02d:%02d", hour, minute)
//
//        // 알람 해제 버튼을 생성합니다.
//        val dismissIntent = Intent(context, DismissAlarmActivity::class.java)
//        val dismissPendingIntent = PendingIntent.getActivity(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        // Notification을 생성합니다.
//        val builder = NotificationCompat.Builder(context, "ALARM_CHANNEL")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("알람")
//            .setContentText(timeText)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .addAction(R.drawable.ic_launcher_foreground, "알람 해제", dismissPendingIntent)
//
//        with(NotificationManagerCompat.from(context)) {
//            notify(0, builder.build())
//        }
//    }
//}


//class MyBroadcastReceiver : BroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        // 알람 울릴 때 실행할 작업을 여기에 작성합니다.
//        // 예를 들어, 다음과 같이 알람을 울리도록 할 수 있습니다.
//        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        val ringtone = RingtoneManager.getRingtone(context, uri)
//        ringtone.play()
//
//        // 알람 시간을 표시합니다.
//        val calendar = Calendar.getInstance()
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//        val timeText = String.format("%02d:%02d", hour, minute)
//
//        // 알람 해제 버튼을 생성합니다.
//        val dismissIntent = Intent(context, DismissAlarmActivity::class.java)
//        val dismissPendingIntent = PendingIntent.getActivity(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        // Notification을 생성합니다.
//        val builder = NotificationCompat.Builder(context, "ALARM_CHANNEL")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("알람")
//            .setContentText(timeText)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .addAction(R.drawable.ic_launcher_foreground, "알람 해제", dismissPendingIntent)
//
//        with(NotificationManagerCompat.from(context)) {
//            notify(0, builder.build())
//        }
//    }
//}
