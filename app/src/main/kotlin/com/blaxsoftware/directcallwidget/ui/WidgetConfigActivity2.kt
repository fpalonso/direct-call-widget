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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickContact
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.viewmodel.WidgetConfigViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import dev.ferp.dcw.core.analytics.Analytics
import javax.inject.Inject

// TODO deprecate this class
@AndroidEntryPoint
class WidgetConfigActivity2 : AppCompatActivity(),
    ReadContactsPermissionExplanationDialog.Callback,
    WidgetConfigFragment.Listener {

    private val viewModel: WidgetConfigViewModel by viewModels()

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
            viewModel.onPictureSelected(uri)
        } else {
            firebaseAnalytics.logEvent(Analytics.Event.PICK_IMAGE_CANCEL, null)
        }
    }

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

    fun onPickImageFromGalleryClick() {
        firebaseAnalytics.logEvent(Analytics.Event.PICK_IMAGE_CLICK, null)
        pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    override fun onReadContactExplanationClosed() {
        requestContactPermission.launch(Manifest.permission.READ_CONTACTS)
    }

    override fun onAccept() {
        val dataIntent = Intent()
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, viewModel.widgetId)
        setResult(Activity.RESULT_OK, dataIntent)
        finish()
    }

    override fun onCancel() {
        finish()
    }
}