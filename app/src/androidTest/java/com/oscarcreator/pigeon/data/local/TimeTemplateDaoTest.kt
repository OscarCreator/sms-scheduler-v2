package com.oscarcreator.pigeon.data.local

import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.data.timetemplate.local.TimeTemplateDao
import com.oscarcreator.pigeon.util.observeOnce
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
        val timeTemplate = TimeTemplate(timeTemplateId = 4, delay = -6)
        assertThat(timeTemplateDao.insert(timeTemplate), `is`(4))

        timeTemplateDao.observeTimeTemplates().observeOnce {
            assertThat(it, `is`(listOf(timeTemplate)))
        }
    }

    @Test
    fun timeTemplate_insertedAndDeleted_returnsEmpty() = runBlocking {
        val timeTemplate = TimeTemplate(timeTemplateId = 5, delay = 600)
        assertThat(timeTemplateDao.insert(timeTemplate), `is`(5))
        assertThat(timeTemplateDao.delete(timeTemplate), `is`(1))

        timeTemplateDao.observeTimeTemplates().observeOnce {
            assertThat(it, `is`(emptyList()))
        }
    }

    @Test
    fun timeTemplate_insertedAndUpdated_returnsUpdated() = runBlocking {
        val timeTemplate = TimeTemplate(timeTemplateId = 100, delay = 600)
        assertThat(timeTemplateDao.insert(timeTemplate), `is`(100))

        val updatedTimeTemplate = TimeTemplate(timeTemplateId = 100, delay = 600)

        assertThat(timeTemplateDao.update(updatedTimeTemplate), `is`(1))

        timeTemplateDao.observeTimeTemplates().observeOnce {
            assertThat(it, `is`(listOf(updatedTimeTemplate)))
        }
    }

}