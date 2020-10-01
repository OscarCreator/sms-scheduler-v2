package com.oscarcreator.sms_scheduler_v2.data

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    CustomerTest::class,
    TreatmentTest::class,
    MessageTest::class,
    TimeTemplateTest::class,
    ScheduledTreatmentTest::class,
    ScheduledTreatmentCustomerCrossRefTest::class
)
class EntityTestSuit