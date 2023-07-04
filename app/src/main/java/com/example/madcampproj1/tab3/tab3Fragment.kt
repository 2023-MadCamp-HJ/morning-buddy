package com.example.madcampproj1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.example.madcampproj1.tab3.MyBroadcastReceiver
import java.util.*

class tab3Fragment : Fragment() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab3, container, false)

        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timePicker = view.findViewById<TimePicker>(R.id.timepicker)
        val alarmButton = view.findViewById<Button>(R.id.alarmbutton)

        alarmButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            if (Build.VERSION.SDK_INT >= 23) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.hour, timePicker.minute, 0)
            } else {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.currentHour, timePicker.currentMinute, 0)
            }

            setAlarm(calendar.timeInMillis)
        }

        return view
    }

    private fun setAlarm(timeInMillis: Long) {
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }
}
