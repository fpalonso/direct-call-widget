/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2025 Fer P. A.
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

package com.blaxsoftware.directcallwidget.onecontactwidget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.blaxsoftware.directcallwidget.legacy.LegacyWidgets
import dagger.hilt.android.AndroidEntryPoint
import dev.ferp.dcw.feature.contactconfig.ContactConfigScreen
import dev.ferp.dcw.feature.contactconfig.rememberContactConfigState
import dev.ferp.dcw.feature.onecontactwidget.OneContactConfigViewModel
import javax.inject.Inject

@AndroidEntryPoint
class OneContactConfigActivity : ComponentActivity() {

    private val viewModel: OneContactConfigViewModel by viewModels()

    @Inject
    lateinit var legacyWidgets: LegacyWidgets

    private val appWidgetId: Int
        get() = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContent {
            MaterialTheme {
                val contactConfigState = rememberContactConfigState()
                ContactConfigScreen(
                    state = contactConfigState,
                    onSave = {
                        viewModel.saveWidget(
                            appWidgetId = appWidgetId,
                            displayName = contactConfigState.displayName,
                            phoneNumber = contactConfigState.selectedPhoneNumber.orEmpty(),
                            pictureUri = contactConfigState.pictureUri
                        )
                        legacyWidgets.updateAll()
                        finishWidgetConfiguration()
                    },
                    onDismiss = ::finish
                )
            }
        }
    }

    private fun finishWidgetConfiguration() {
        val resultData = Intent()
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultData)
        finish()
    }
}