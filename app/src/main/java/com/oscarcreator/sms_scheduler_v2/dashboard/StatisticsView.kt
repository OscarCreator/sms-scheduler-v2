package com.oscarcreator.sms_scheduler_v2.dashboard

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R


class StatisticsView : MaterialCardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var tvTotalTreatments: MaterialTextView
    private var tvProcentTreatments: MaterialTextView
    private var tvTotalEarnings: MaterialTextView
    private var tvTotalEarningsExclusive: MaterialTextView

    init {

        inflate(context, R.layout.statistics_view, this)

        tvTotalTreatments = findViewById(R.id.tv_total_treatments)
        tvProcentTreatments = findViewById(R.id.tv_procent_treatments)
        tvTotalEarnings = findViewById(R.id.tv_total_earnings)
        tvTotalEarningsExclusive = findViewById(R.id.tv_total_earnings_exclusive)

    }


    fun setTotalTreatments(value: Int){
        tvTotalTreatments.text = value.toString()
        tvTotalTreatments.invalidate()
    }

    fun setProcentTreatments(increase: Int){
        tvProcentTreatments.text = resources.getString(R.string.increase_procent_treatments, increase)
        tvProcentTreatments.invalidate()
    }

    fun setTotalEarnings(value: Int){
        tvTotalEarnings.text = resources.getString(R.string.total_earning, value)
        tvTotalEarnings.invalidate()
    }

    fun setTotalEarningsExclusive(value: Int){
        tvTotalEarningsExclusive.text = resources.getString(R.string.total_earning_exclusive, value)
        tvTotalEarningsExclusive.invalidate()
    }

}