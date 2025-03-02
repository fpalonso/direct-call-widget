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

package dev.ferp.dcw.feature.onecontactwidget.config

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.ferp.dcw.core.ui.contactconfig.ContactConfigScreen
import dev.ferp.dcw.core.ui.contactconfig.ContactConfigUiState
import dev.ferp.dcw.core.ui.contactconfig.ContactConfigViewModel

@AndroidEntryPoint
class OneContactConfigActivity : AppCompatActivity() {

    private val contactConfigViewModel: ContactConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val uiState by contactConfigViewModel.uiState
                    .collectAsStateWithLifecycle(ContactConfigUiState())
                ContactConfigScreen(
                    title = "", // FIXME
                    uiState,
                    onImageUriChanged = contactConfigViewModel::onImageUriChanged,
                    onDisplayNameChanged = contactConfigViewModel::onDisplayNameChanged,
                    onPhoneNumberChanged = contactConfigViewModel::onPhoneNumberChanged
                )
            }
        }
    }
}