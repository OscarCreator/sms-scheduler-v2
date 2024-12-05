package com.oscarcreator.pigeon.data.local

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ContactDaoTest::class,
    TreatmentDaoTest::class,
    MessagesDaoTest::class,
    TimeTemplateDaoTest::class,
    ScheduledTreatmentDaoTest::class
)
class DaoTestSuit