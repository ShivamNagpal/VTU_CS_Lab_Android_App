package com.nagpal.shivam.vtucslab.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nagpal.shivam.vtucslab.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.coordinator.toolbar)
    }
}
