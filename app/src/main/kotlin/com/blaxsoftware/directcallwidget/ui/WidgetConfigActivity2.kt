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
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickContact
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.analytics.Analytics
import com.blaxsoftware.directcallwidget.appwidget.DirectCallWidgetProvider
import com.blaxsoftware.directcallwidget.data.source.SingleContactWidgetRepository
import com.blaxsoftware.directcallwidget.file.Files
import com.blaxsoftware.directcallwidget.viewmodel.ConfigResult
import com.blaxsoftware.directcallwidget.viewmodel.WidgetConfigViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

// TODO deprecate this class
@AndroidEntryPoint
class WidgetConfigActivity2 : AppCompatActivity(),
        ReadContactsPermissionExplanationDialog.Callback,
        ChangePictureOptionsDialog.ChangePictureListener {

    private val viewModel: WidgetConfigViewModel by viewModels()

    // TODO inject this in viewModel
    @Inject lateinit var singleContactWidgetRepo: SingleContactWidgetRepository

    private val requestContactPermission = registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            firebaseAnalytics.logEvent(Analytics.Event.GRANT_CONTACT_PERMISSION, null)
            pickContact.launch(null)
        } else {
            firebaseAnalytics.logEvent(Analytics.Event.DENY_CONTACT_PERMISSION, null)
        }
    }

    private val pickContact = registerForActivityResult(PickContact()) { contactUri: Uri? ->
        contactUri?.let {
            firebaseAnalytics.logEvent(Analytics.Event.PICK_CONTACT, null)
            viewModel.loadContact(it)
        } ?: firebaseAnalytics.logEvent(Analytics.Event.PICK_CONTACT_CANCEL, null)
    }

    private val pickImage = registerForActivityResult(PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            firebaseAnalytics.logEvent(Analytics.Event.PICK_IMAGE, null)
        } else {
            firebaseAnalytics.logEvent(Analytics.Event.PICK_IMAGE_CANCEL, null)
        }
        viewModel.onPictureSelected(uri)
    }

    private val takePicture = registerForActivityResult(TakePicture()) { result: Boolean ->
        if (result) {
            firebaseAnalytics.logEvent(Analytics.Event.TAKE_PICTURE, null)
            viewModel.onPictureSelected(cameraOutputUri)
        } else {
            firebaseAnalytics.logEvent(Analytics.Event.TAKE_PICTURE_CANCEL, null)
        }
    }

    private var cameraOutputUri: Uri? = null
    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = bundleOf(
            FirebaseAnalytics.Param.SCREEN_NAME to "Widget Setup",
            FirebaseAnalytics.Param.SCREEN_CLASS to WidgetConfigActivity2::class.simpleName
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        setContentView(R.layout.activity_widget_config2)

        viewModel.widgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        )

        setResult(Activity.RESULT_CANCELED)
        if (savedInstanceState != null) return

        pickContact()
    }

    @Suppress("DEPRECATION")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        cameraOutputUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getParcelable(STATE_CAMERA_OUTPUT_URI, Uri::class.java)
        } else {
            savedInstanceState.getParcelable(STATE_CAMERA_OUTPUT_URI)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(STATE_CAMERA_OUTPUT_URI, cameraOutputUri)
        super.onSaveInstanceState(outState)
    }

    private fun pickContact() {
        when {
            ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                val params = bundleOf(
                    FirebaseAnalytics.Param.SCREEN_NAME to "Pick Contact"
                )
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
                pickContact.launch(null)
            }
            shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS) -> {
                ReadContactsPermissionExplanationDialog().show(
                        supportFragmentManager,
                        "readContactsPermissionExplanation"
                )
            }
            else -> requestContactPermission.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    override fun onTakePictureClick() {
        lifecycleScope.launch {
            try {
                val outputFile = Files.createCameraOutputFile(this@WidgetConfigActivity2)
                outputFile?.let {
                    cameraOutputUri = FileProvider.getUriForFile(
                            this@WidgetConfigActivity2,
                            "$packageName.fileprovider",
                            outputFile
                    )
                    cameraOutputUri?.let {
                        takePicture.launch(it)
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(
                        this@WidgetConfigActivity2,
                        R.string.error_using_camera,
                        Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    override fun onPickImageFromGalleryClick() {
        firebaseAnalytics.logEvent(Analytics.Event.PICK_IMAGE_CLICK, null)
        pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    override fun onStart() {
        super.onStart()

        viewModel.result.observe(this) { result ->
            if (result.accepted) {
                updateWidget(result)
                viewModel.widgetId?.let { widgetId ->
                    Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId).also { data ->
                        setResult(Activity.RESULT_OK, data)
                    }
                }
            }
            finish()
        }
    }

    private fun updateWidget(result: ConfigResult) {
        result.widgetData?.let { widgetData ->
            FirebaseCrashlytics.getInstance().log("Widget data accepted from WidgetConfigActivity2")
            DirectCallWidgetProvider.setWidgetData(
                    applicationContext,
                    singleContactWidgetRepo,
                    AppWidgetManager.getInstance(this),
                    widgetData.widgetId,
                    widgetData
            )
        }
    }

    override fun onReadContactExplanationClosed() {
        requestContactPermission.launch(Manifest.permission.READ_CONTACTS)
    }

    companion object {
        const val STATE_CAMERA_OUTPUT_URI = "cameraOutputUri"
    }
}