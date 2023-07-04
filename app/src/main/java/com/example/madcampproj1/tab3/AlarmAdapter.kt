package com.example.madcampproj1.tab3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampproj1.AlarmData
import com.example.madcampproj1.databinding.ItemAlarmBinding
import com.example.madcampproj1.tab3Fragment

class AlarmAdapter(private val alarmList: List<AlarmData>, private val fragment: tab3Fragment) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarmData = alarmList[position]
        val amPm = if (alarmData.hour < 12) "오전" else "오후"
        val hour = if (alarmData.hour % 12 == 0) 12 else alarmData.hour % 12
        holder.binding.timeText.text = String.format("%s %d:%02d", amPm, hour, alarmData.minute)

        // null 추가
        holder.binding.alarmSwitch.setOnCheckedChangeListener(null)
        holder.binding.alarmSwitch.isChecked = alarmData.isEnabled

        holder.binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fragment.setAlarm(alarmData)
            } else {
                fragment.cancelAlarm(alarmData)
            }
            alarmData.isEnabled = isChecked
        }
    }

    override fun getItemCount() = alarmList.size
}


//class AlarmAdapter(private val alarmList: List<AlarmData>, private val fragment: tab3Fragment) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
//
//    class ViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val alarmData = alarmList[position]
//        holder.binding.timeText.text = String.format("%02d:%02d", alarmData.hour, alarmData.minute)
//
//        holder.binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                fragment.setAlarm(alarmData)
//            } else {
//                fragment.cancelAlarm(alarmData)
//            }
//        }
//    }
//
//    override fun getItemCount() = alarmList.size
//}
