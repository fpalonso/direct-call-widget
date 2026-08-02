/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2024 Fer P. A.
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

package com.blaxsoftware.directcallwidget.ui.multicontact.config

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blaxsoftware.directcallwidget.data.ContactConfig
import com.blaxsoftware.directcallwidget.data.MultiContactInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ferp.dcw.core.domain.picture.AddPictureUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MultiContactConfigUiState(
    val contacts: List<ContactConfig> = emptyList()
)

@HiltViewModel
class MultiContactConfigViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val addPictureUseCase: AddPictureUseCase<Bitmap>
) : ViewModel() {

    private var contacts: List<ContactConfig>
        get() = savedStateHandle.get<List<ContactConfig>>(KEY_CONTACTS) ?: emptyList()
        set(value) {
            savedStateHandle[KEY_CONTACTS] = value
            uiState = uiState.copy(contacts = value)
        }

    var uiState by mutableStateOf(MultiContactConfigUiState(contacts = contacts))
        private set

    fun addContact(contactConfig: ContactConfig) {
        viewModelScope.launch {
            val internalPictureUri = addPictureUseCase(contactConfig.pictureUri)
                .getOrNull() ?: contactConfig.pictureUri

            contacts = contacts + contactConfig.copy(
                pictureUri = internalPictureUri
            )
        }
    }

    fun buildWidgetInfo() = MultiContactInfo.Available(
        contactList = uiState.contacts
    )

    companion object {
        private const val KEY_CONTACTS = "contacts"
    }
}
