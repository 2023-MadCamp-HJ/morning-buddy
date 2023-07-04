package com.example.madcampproj1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampproj1.databinding.FragmentTab3Binding
import com.example.madcampproj1.tab3.AlarmAdapter
import com.example.madcampproj1.tab3.MyBroadcastReceiver
import com.example.madcampproj1.tab3.TimePickerFragment
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

//data class AlarmData(
//    val id: Int,
//    val hour: Int,
//    val minute: Int,
//    val days: BooleanArray
//)
data class AlarmData(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val days: BooleanArray,
    var isEnabled: Boolean = true
)

class tab3Fragment : Fragment() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    internal val alarmList = mutableListOf<AlarmData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTab3Binding.inflate(inflater, container, false)

        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val sharedPreferences = context?.getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val alarmsJson = sharedPreferences?.getString("alarms", "[]")
        val alarmsArray = JSONArray(alarmsJson)
        for (i in 0 until alarmsArray.length()) {
            val alarmJson = alarmsArray.getJSONObject(i)
            val id = alarmJson.getInt("id")
            val hour = alarmJson.getInt("hour")
            val minute = alarmJson.getInt("minute")
            val isEnabled = alarmJson.getBoolean("isEnabled")
            val daysJson = alarmJson.getJSONArray("days")
            val days = BooleanArray(7) { false }
            for (j in 0 until daysJson.length()) {
                days[j] = daysJson.getBoolean(j)
            }
            val alarmData = AlarmData(id, hour, minute, days, isEnabled)
            alarmList.add(alarmData)
        }

        binding.addAlarmButton.setOnClickListener {
            val newFragment = TimePickerFragment()
            newFragment.show(childFragmentManager, "timePicker")
        }

        alarmList.sortWith(compareBy({ it.hour }, { it.minute }))

        val adapter = AlarmAdapter(alarmList, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val alarmsArray = JSONArray()
        for (alarmData in alarmList) {
            val alarmJson = JSONObject()
            alarmJson.put("id", alarmData.id)
            alarmJson.put("hour", alarmData.hour)
            alarmJson.put("minute", alarmData.minute)
            alarmJson.put("isEnabled", alarmData.isEnabled)
            val daysJson = JSONArray()
            for (day in alarmData.days) {
                daysJson.put(day)
            }
            alarmJson.put("days", daysJson)
            alarmsArray.put(alarmJson)
        }
        val alarmsJson = alarmsArray.toString()

        val sharedPreferences = context?.getSharedPreferences("alarms", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putString("alarms", alarmsJson)?.apply()
    }


    internal fun setAlarm(alarmData: AlarmData) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarmData.hour)
            set(Calendar.MINUTE, alarmData.minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, MyBroadcastReceiver::class.java).apply {
            putExtra("ALARM_ID", alarmData.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, alarmData.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    internal fun cancelAlarm(alarmData: AlarmData) {
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmData.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)
    }
}
