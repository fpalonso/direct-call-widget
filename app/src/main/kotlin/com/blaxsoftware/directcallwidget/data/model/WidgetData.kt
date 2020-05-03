package com.blaxsoftware.directcallwidget.data.model

data class WidgetData(
        val widgetId: Int,
        val displayName: String?,
        val phoneNumber: String,
        val phoneType: Int,
        val pictureUri: String?
) {
    val hasDisplayName: Boolean
        get() = displayName?.isNotEmpty() == true
}
