package com.oscarcreator.sms_scheduler_v2.data.local

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    CustomerDaoTest::class,
    TreatmentDaoTest::class,
    MessagesDaoTest::class,
    TimeTemplateDaoTest::class,
    ScheduledTreatmentDaoTest::class
)
class DaoTestSuit