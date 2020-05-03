package com.blaxsoftware.directcallwidget

import android.content.Context
import com.blaxsoftware.directcallwidget.data.source.*
import java.io.File

interface ServiceLocator {
    val contactDataSource: ContactDataSource
    val widgetDataSource: WidgetDataSource
    val widgetPicDataSource: WidgetPicDataSource

    companion object {
        private val LOCK = Any()
        private lateinit var instance: DefaultServiceLocator

        fun getInstance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (!::instance.isInitialized) {
                    instance = DefaultServiceLocator(context.applicationContext)
                }
                return instance
            }
        }
    }
}

class DefaultServiceLocator(private val context: Context) : ServiceLocator {

    override val contactDataSource: ContactDataSource by lazy {
        ContactRepository(context.contentResolver)
    }

    override val widgetDataSource: WidgetDataSource by lazy {
        WidgetDataRepository(context.getSharedPreferences(
                "widget_data",
                Context.MODE_PRIVATE
        ))
    }

    override val widgetPicDataSource: WidgetPicDataSource by lazy {
        WidgetPicRepository(
                context.contentResolver,
                picsDir = File(context.filesDir, "Pictures")
        )
    }
}

val Context.serviceLocator: ServiceLocator
    get() = ServiceLocator.getInstance(this)

val Context.contactRepository: ContactDataSource
    get() = serviceLocator.contactDataSource

val Context.widgetRepository: WidgetDataSource
    get() = serviceLocator.widgetDataSource

val Context.widgetPicRepository: WidgetPicDataSource
    get() = serviceLocator.widgetPicDataSource
