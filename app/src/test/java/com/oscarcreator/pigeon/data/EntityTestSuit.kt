package com.oscarcreator.pigeon.data

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ContactTest::class,
    TreatmentTest::class,
    MessageTest::class,
    TimeTemplateTest::class,
    ScheduledTreatmentTest::class
)
class EntityTestSuit