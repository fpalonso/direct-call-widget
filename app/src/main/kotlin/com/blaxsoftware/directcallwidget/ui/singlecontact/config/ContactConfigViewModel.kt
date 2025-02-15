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

package com.blaxsoftware.directcallwidget.ui.singlecontact.config

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ferp.dcw.data.contacts.ContactRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactConfigUiState(
    val pictureUri: Uri? = null,
    val displayName: String = "",
    val phoneNumber: String = "",
)

@HiltViewModel
class ContactConfigViewModel @Inject constructor(
    private val contactRepo: ContactRepository<Uri>
) : ViewModel() {

    var uiState: ContactConfigUiState by mutableStateOf(ContactConfigUiState())
        private set

    fun onPictureUriChanged(uri: Uri?) {
        uiState = uiState.copy(pictureUri = uri)
    }

    fun onDisplayNameChanged(displayName: String) {
        uiState = uiState.copy(displayName = displayName)
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        uiState = uiState.copy(phoneNumber = phoneNumber)
    }

    fun onContactPicked(contactUri: Uri?) {
        if (contactUri == null) return
        viewModelScope.launch {
            val contact = contactRepo.getContactById(contactUri) ?: return@launch
            uiState = uiState.copy(
                pictureUri = contact.photoUri?.toUri(),
                displayName = contact.displayName,
                // FIXME
                phoneNumber = ""
            )
        }
    }
}