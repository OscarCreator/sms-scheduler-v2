package com.oscarcreator.sms_scheduler_v2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oscarcreator.sms_scheduler_v2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.statisticsView.apply {
            setTotalTreatments(150)
            setProcentTreatments(9)
            setTotalEarnings(4250)
            setTotalEarningsExclusive(2530)
        }

        setContentView(binding.root)
    }
}