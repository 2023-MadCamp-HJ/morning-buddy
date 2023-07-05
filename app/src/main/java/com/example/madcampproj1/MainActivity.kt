package com.example.madcampproj1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.madcampproj1.databinding.ActivityMainBinding
import com.example.madcampproj1.tab1.tab1Fragment
import com.example.madcampproj1.tab2.FragmentAdapter
import com.example.madcampproj1.tab2.tab2Fragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fragmentList = listOf(tab1Fragment(), tab2Fragment(), tab3Fragment())

        val adapter = FragmentAdapter(this)
        adapter.fragmentList = fragmentList

        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false

        // Tab Title 설정
        val tabTitles = listOf("연락처", "갤러리", "알람")
//        val tabIcons = listOf(R.drawable.contact, R.drawable.gallery, R.drawable.question)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
            tab.text = tabTitles[position]
//            tab.setIcon(tabIcons[position])
        }.attach()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WAKE_LOCK), 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "알람"
            val descriptionText = "알람 채널입니다."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("ALARM_CHANNEL", name, importance).apply {
                var description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}