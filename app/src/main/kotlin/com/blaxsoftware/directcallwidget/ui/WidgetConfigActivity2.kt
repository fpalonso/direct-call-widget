/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.blaxsoftware.directcallwidget.ui

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.PickContact
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider
import com.blaxsoftware.directcallwidget.viewmodel.ViewModelFactory
import com.blaxsoftware.directcallwidget.viewmodel.WidgetConfigViewModel

class WidgetConfigActivity2 : AppCompatActivity(),
        ReadContactsPermissionExplanationDialog.Callback {

    private val viewModel by viewModels<WidgetConfigViewModel> {
        ViewModelFactory(applicationContext)
    }

    private val requestContactPermission = registerForActivityResult(RequestPermission()) { isGranted: Boolean? ->
        if (isGranted == true) {
            pickContact.launch(null)
        } else {
            finish()
        }
    }

    private val pickContact = registerForActivityResult(PickContact()) { contactUri: Uri? ->
        contactUri?.let {
            viewModel.loadContact(it)
        } ?: finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config2)

        viewModel.widgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        )

        setResult(Activity.RESULT_CANCELED)
        if (savedInstanceState != null) return

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED -> {
                pickContact.launch(null)
            }
            shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) -> {
                ReadContactsPermissionExplanationDialog().show(
                        supportFragmentManager,
                        "readContactsPermissionExplanation"
                )
            }
            else -> {
                requestContactPermission.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.result.observe(this, Observer { result ->
            if (result.accepted) {
                result.widgetData?.let { widgetData ->
                    DirectCallWidgetProvider.setWidgetData(
                            applicationContext,
                            AppWidgetManager.getInstance(this),
                            widgetData.widgetId,
                            widgetData
                    )
                }
                viewModel.widgetId?.let { widgetId ->
                    Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId).also { data ->
                        setResult(Activity.RESULT_OK, data)
                    }
                }
            }
            finish()
        })
    }

    override fun onReadContactExplanationClosed() {
        requestContactPermission.launch(Manifest.permission.READ_CONTACTS)
    }
}