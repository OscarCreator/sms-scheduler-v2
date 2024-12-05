package com.oscarcreator.pigeon.dashboard

/*
TODO not used yet
@RunWith(AndroidJUnit4::class)
class StatisticsViewTest {

    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var instrumentationContext: Context

    @Before
    fun setup(){
        dashboardViewModel = DashboardViewModel(TreatmentsStatsRepository.getInstance())
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun viewIsDisplayed(){
        onView(withId(R.id.statistics_view)).check(matches(isDisplayed()))
    }

    @Test
    fun totalTreatments_isDisplayed() {
        val treatments = 100
        dashboardViewModel.setTotalTreatments(treatments)
        onView(withId(R.id.tv_total_treatments)).check(matches(withMaterialText(treatments.toString())))
    }

    @Test
    fun procentTreatments_isDisplayed() {
        val procent = 11
        dashboardViewModel.setProcentTreatments(procent)
        val text = instrumentationContext.getString(R.string.increase_procent_treatments, procent)
        onView(withId(R.id.tv_procent_treatments)).check(matches(withMaterialText(text)))

    }

    @Test
    fun totalEarnings_isDisplayed() {
        val earnings = 4300
        dashboardViewModel.setTotalEarnings(earnings)
        val text = instrumentationContext.getString(R.string.total_earning, earnings)
        onView(withId(R.id.tv_total_earnings)).check(matches(withMaterialText(text)))
    }

    @Test
    fun totalEarningsExclusive_isDisplayed() {
        val earningsExclusive = 1400
        dashboardViewModel.setTotalEarningsExclusive(earningsExclusive)
        val text = instrumentationContext.getString(R.string.total_earning_exclusive, earningsExclusive)
        onView(withId(R.id.tv_total_earnings_exclusive)).check(matches(withMaterialText(text)))
    }

    @Test
    fun treatmentText_isDisplayed(){
        val text = instrumentationContext.getString(R.string.services)
        onView(withId(R.id.tv_treatment_text)).check(matches(withMaterialText(text)))
    }
}

 */