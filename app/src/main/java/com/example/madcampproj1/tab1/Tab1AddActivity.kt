package com.example.madcampproj1.tab1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.madcampproj1.R
import com.example.madcampproj1.databinding.ActivityTab1EditBinding

class Tab1AddActivity : AppCompatActivity() {

    private lateinit var binding:ActivityTab1EditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)



    }
}