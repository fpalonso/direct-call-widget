package com.blaxsoftware.directcallwidget.data.source

import com.blaxsoftware.directcallwidget.SharedPreferencesRule
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test

class WidgetDataRepositoryTest {

    @get:Rule
    val sharedPreferencesRule = SharedPreferencesRule("testPrefs.txt")

    private val dataSource: WidgetDataSource by lazy {
        WidgetDataRepository(sharedPreferencesRule.sharedPreferences)
    }

    @Test
    fun writeAndReadWidgetDataFromSharedPreferences() {
        with(dataSource) {
            insertWidgetData(fakeWidgetData)
            getWidgetDataById(fakeWidgetData.widgetId).also {
                Truth.assertThat(it).isEqualTo(fakeWidgetData)
            }
        }
    }

    @Test
    fun writeAndDeleteWidgetDataFromSharedPreferences() {
        with(dataSource) {
            insertWidgetData(fakeWidgetData)
            deleteWidgetDataById(fakeWidgetData.widgetId)
            getWidgetDataById(fakeWidgetData.widgetId).also {
                Truth.assertThat(it).isNull()
            }
        }
    }
}
