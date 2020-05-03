package com.blaxsoftware.directcallwidget.data.source

import android.content.SharedPreferences
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.core.content.edit
import com.blaxsoftware.directcallwidget.data.model.WidgetData

/**
 * Data source class that retrieves widget data from the given SharedPreferences object.
 */
class WidgetDataRepository(private val preferences: SharedPreferences) : WidgetDataSource {

    override fun getWidgetDataById(widgetId: Int): WidgetData? = with(preferences) {
        if (contains("$ATTR_DISPLAY_NAME$widgetId")
                || contains("$ATTR_PHONE_NUMBER$widgetId")
                || contains("$ATTR_PHONE_TYPE$widgetId")
                || contains("$ATTR_PICTURE_URI$widgetId")) {
            WidgetData(
                    widgetId = widgetId,
                    displayName = getString("$ATTR_DISPLAY_NAME$widgetId", null),
                    phoneNumber = getString("$ATTR_PHONE_NUMBER$widgetId", "") ?: "",
                    phoneType = getInt("$ATTR_PHONE_TYPE$widgetId", Phone.TYPE_HOME),
                    pictureUri = getString("$ATTR_PICTURE_URI$widgetId", null)
            )
        } else null
    }

    override fun insertWidgetData(widgetData: WidgetData) = with(widgetData) {
        preferences.edit {
            putString("$ATTR_DISPLAY_NAME$widgetId", displayName)
            putString("$ATTR_PHONE_NUMBER$widgetId", phoneNumber)
            putInt("$ATTR_PHONE_TYPE$widgetId", phoneType)
            putString("$ATTR_PICTURE_URI$widgetId", pictureUri)
        }
    }

    override fun deleteWidgetDataById(widgetId: Int) {
        preferences.edit {
            remove("$ATTR_DISPLAY_NAME$widgetId")
            remove("$ATTR_PHONE_NUMBER$widgetId")
            remove("$ATTR_PHONE_TYPE$widgetId")
            remove("$ATTR_PICTURE_URI$widgetId")
        }
    }

    private companion object {
        private const val ATTR_DISPLAY_NAME = "name_"
        private const val ATTR_PHONE_NUMBER = "phone_"
        private const val ATTR_PHONE_TYPE = "phone_type_"
        private const val ATTR_PICTURE_URI = "pic_"
    }
}
