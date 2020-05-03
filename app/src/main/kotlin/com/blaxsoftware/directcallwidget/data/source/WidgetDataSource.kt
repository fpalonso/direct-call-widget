package com.blaxsoftware.directcallwidget.data.source

import com.blaxsoftware.directcallwidget.data.model.WidgetData

interface WidgetDataSource {

    fun getWidgetDataById(widgetId: Int): WidgetData?

    fun insertWidgetData(widgetData: WidgetData)

    fun deleteWidgetDataById(widgetId: Int)
}
