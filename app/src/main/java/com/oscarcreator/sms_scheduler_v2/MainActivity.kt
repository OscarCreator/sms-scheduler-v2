package com.oscarcreator.sms_scheduler_v2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.oscarcreator.sms_scheduler_v2.data.TreatmentsStatsRepository
import com.oscarcreator.sms_scheduler_v2.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainActivityViewModel = MainActivityViewModel(TreatmentsStatsRepository.getInstance())


        mainActivityViewModel.apply {
            setTotalTreatments(140)
            setProcentTreatments(9)
            setTotalEarnings(4250)
            setTotalEarningsExclusive(2530)

            totalTreatments.observe(this@MainActivity, {
                binding.statisticsView.setTotalTreatments(it)
            })

            procentTreatments.observe(this@MainActivity, {
                binding.statisticsView.setProcentTreatments(it)
            })

            totalEarnings.observe(this@MainActivity, {
                binding.statisticsView.setTotalEarnings(it)
            })

            totalEarningsExclusive.observe(this@MainActivity, {
                binding.statisticsView.setTotalEarningsExclusive(it)
            })

        }
    }
}