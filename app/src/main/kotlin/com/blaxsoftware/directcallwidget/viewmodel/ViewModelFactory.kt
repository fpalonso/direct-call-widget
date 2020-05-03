package com.blaxsoftware.directcallwidget.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blaxsoftware.directcallwidget.ServiceLocator
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val serviceLocator: ServiceLocator by lazy {
        ServiceLocator.getInstance(context)
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WidgetConfigViewModel::class.java)) {
            WidgetConfigViewModel(
                    serviceLocator.contactDataSource,
                    serviceLocator.widgetDataSource,
                    serviceLocator.widgetPicDataSource
            ) as T
        } else throw IllegalArgumentException("ViewModel not recognized: ${modelClass.name}")
    }
}
