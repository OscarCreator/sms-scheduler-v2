package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplateDao
import com.oscarcreator.sms_scheduler_v2.util.observeOnce
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

class TimeTemplateDaoTest : BaseDaoTest() {

    lateinit var timeTemplateDao: TimeTemplateDao

    @Before
    override fun inititalizeDatabase() {
        super.inititalizeDatabase()
        timeTemplateDao = database.timeTemplateDao()
    }

    @Test
    fun timeTemplate_insertedInDatabase_returnsTimeTemplate() = runBlocking {
        val timeTemplate = TimeTemplate(id = 4, delay = -6)
        assertThat(timeTemplateDao.insert(timeTemplate), `is`(4))

        timeTemplateDao.getTimeTemplates().observeOnce {
            assertThat(it, `is`(listOf(timeTemplate)))
        }
    }

    @Test
    fun message_insertedAndDeleted_returnsEmpty() = runBlocking {
        val timeTemplate = TimeTemplate(id = 5, delay = 600)
        assertThat(timeTemplateDao.insert(timeTemplate), `is`(5))
        assertThat(timeTemplateDao.delete(timeTemplate), `is`(1))

        timeTemplateDao.getTimeTemplates().observeOnce {
            assertThat(it, `is`(emptyList()))
        }
    }

    @Test
    fun message_insertedAndUpdated_returnsUpdated() = runBlocking {
        val timeTemplate = TimeTemplate(id = 100, delay = 600)
        assertThat(timeTemplateDao.insert(timeTemplate), `is`(100))

        val updatedTimeTemplate = TimeTemplate(id = 100, delay = 600)

        assertThat(timeTemplateDao.update(updatedTimeTemplate), `is`(1))

        timeTemplateDao.getTimeTemplates().observeOnce {
            assertThat(it, `is`(listOf(updatedTimeTemplate)))
        }
    }

}