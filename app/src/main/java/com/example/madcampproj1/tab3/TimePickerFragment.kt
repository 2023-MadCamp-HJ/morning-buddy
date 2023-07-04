package com.example.madcampproj1.tab3

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampproj1.AlarmData
import com.example.madcampproj1.R
import com.example.madcampproj1.tab3Fragment
import java.util.*
import kotlin.random.Random

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val existingAlarm = (parentFragment as? tab3Fragment)?.alarmList?.find { it.hour == hourOfDay && it.minute == minute }
        if (existingAlarm != null) {
            Toast.makeText(context, "이미 동일한 시각에 설정된 알람이 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val id = Random.nextInt(Int.MAX_VALUE)
        val days = BooleanArray(7) { false }
        val alarmData = AlarmData(id, hourOfDay, minute, days)
        (parentFragment as? tab3Fragment)?.alarmList?.add(alarmData)

        (parentFragment as? tab3Fragment)?.alarmList?.sortWith(compareBy({ it.hour }, { it.minute }))

        (parentFragment as? tab3Fragment)?.setAlarm(alarmData)

        (parentFragment as? tab3Fragment)?.view?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter?.notifyDataSetChanged()
    }

}
