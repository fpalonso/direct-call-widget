package com.blaxsoftware.directcallwidget.data.model

import android.net.Uri

data class Contact(
        val displayName: String,
        val photoUri: Uri?,
        val phoneList: List<Phone>
)

data class Phone(
        val number: String,
        val type: Int
)
