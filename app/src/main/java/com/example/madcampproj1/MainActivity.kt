package com.example.madcampproj1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.madcampproj1.databinding.ActivityMainBinding
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
        val tabTitles = listOf("tab1", "tab2", "tab3")

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}