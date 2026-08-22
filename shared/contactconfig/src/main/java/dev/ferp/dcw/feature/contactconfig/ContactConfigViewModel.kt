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

package dev.ferp.dcw.feature.contactconfig

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ferp.dcw.core.analytics.ContactConfigLogger
import dev.ferp.dcw.core.domain.data.devicecontact.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class InternalContactConfigUiState(
    val pictureUri: String? = null,
    val displayName: String = "",
    val phoneNumber: String = "",
    val errorMessage: Int? = null
)

/**
 * ViewModel for the contact configuration screen.
 */
@HiltViewModel
class ContactConfigViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val logger: ContactConfigLogger
): ViewModel(), ContactConfigLogger by logger {

    private val _uiState = MutableStateFlow(InternalContactConfigUiState())
    internal val uiState = _uiState.asStateFlow()

    init {
        logInit()
    }

    internal fun onPickedContact(contactUri: Uri?) {
        if (contactUri == null) {
            return
        }
        viewModelScope.launch {
            contactRepository.getContactByUri(contactUri.toString()).fold(
                onSuccess = { contact ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            pictureUri = contact.pictureUri,
                            displayName = contact.displayName,
                            phoneNumber = contact.phones.map { it.number }.firstOrNull().orEmpty()
                        )
                    }
                },
                onFailure = {
                    _uiState.update { currentState ->
                        currentState.copy(errorMessage = R.string.error_loading_contact)
                    }
                }
            )
        }
    }

    internal fun onPictureChanged(pictureUri: String?) {
        _uiState.update { currentState ->
            currentState.copy(pictureUri = pictureUri)
        }
    }

    internal fun onDisplayNameChanged(name: String) {
        _uiState.update { currentState ->
            currentState.copy(displayName = name)
        }
    }

    internal fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.update { currentState ->
            currentState.copy(phoneNumber = phoneNumber)
        }
    }
}