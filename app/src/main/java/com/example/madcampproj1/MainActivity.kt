package com.example.madcampproj1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.madcampproj1.databinding.ActivityMainBinding
import com.example.madcampproj1.tab1.tab1Fragment
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
        val tabTitles = listOf("연락처", "갤러리", "탭삽")
//        val tabIcons = listOf(R.drawable.contact, R.drawable.gallery, R.drawable.question)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
            tab.text = tabTitles[position]
//            tab.setIcon(tabIcons[position])
        }.attach()
    }
}