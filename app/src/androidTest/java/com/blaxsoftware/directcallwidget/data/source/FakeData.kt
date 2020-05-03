package com.blaxsoftware.directcallwidget.data.source

import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import androidx.core.net.toUri
import com.blaxsoftware.directcallwidget.data.model.Contact
import com.blaxsoftware.directcallwidget.data.model.Phone
import com.blaxsoftware.directcallwidget.data.model.WidgetData

val fakeWidgetData = WidgetData(
        widgetId = 3,
        displayName = "Alice",
        phoneNumber = "+34 123",
        phoneType = CommonDataKinds.Phone.TYPE_HOME,
        pictureUri = "content://alice.jpg"
)

val fakeContact = Contact(
        "Alice",
        "content://alice.jpg".toUri(),
        listOf(
                Phone("+34 123", CommonDataKinds.Phone.TYPE_HOME),
                Phone("+34 124", CommonDataKinds.Phone.TYPE_MOBILE),
                Phone("+34 125", CommonDataKinds.Phone.TYPE_OTHER)
        )
)
