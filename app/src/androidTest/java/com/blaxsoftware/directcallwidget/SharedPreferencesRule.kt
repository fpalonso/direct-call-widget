package com.blaxsoftware.directcallwidget

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class SharedPreferencesRule(fileName: String) : TestWatcher() {

    val sharedPreferences: SharedPreferences = ApplicationProvider
            .getApplicationContext<Context>()
            .getSharedPreferences(fileName, Context.MODE_PRIVATE)

    override fun finished(description: Description?) {
        super.finished(description)
        sharedPreferences.edit { clear() }
    }
}
