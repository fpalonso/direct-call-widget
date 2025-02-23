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

package dev.ferp.dcw.core.ui.contactconfig

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ContactConfigUiState(
    val imageUri: Uri? = null,
    val displayName: String = "",
    val phoneNumbers: List<String> = emptyList(),
    val selectedPhoneNumber: String = ""
)

@HiltViewModel
class ContactConfigViewModel @Inject constructor() : ViewModel() {

    private val _imageUri = MutableStateFlow<Uri?>(null)
    private val _displayName = MutableStateFlow("")
    private val _phoneNumbers = MutableStateFlow(listOf(""))
    private val _selectedPhoneNumber = MutableStateFlow("")

    val uiState = combine(
        _imageUri, _displayName, _phoneNumbers, _selectedPhoneNumber
    ) { imageUri, displayName, phoneNumbers, selectedPhoneNumber ->
        ContactConfigUiState(
            imageUri, displayName, phoneNumbers, selectedPhoneNumber
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ContactConfigUiState()
    )

    fun onImageUriChanged(imageUri: Uri?) {
        _imageUri.value = imageUri
    }

    fun onDisplayNameChanged(displayName: String) {
        _displayName.value = displayName
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        _selectedPhoneNumber.value = phoneNumber
    }
}