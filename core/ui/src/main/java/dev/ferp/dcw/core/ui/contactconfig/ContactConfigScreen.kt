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

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ferp.dcw.core.ui.R
import dev.ferp.dcw.core.ui.theme.DirectCallWidgetTheme

/**
 * A state object that can be hoisted to observe the current state of the contact configuration.
 */
@Stable
interface ContactConfigState {
    var pictureUri: Uri?
    var displayName: String?
    var phoneNumbers: List<String>
    var selectedPhoneNumber: String?
}

@Stable
private class ContactConfigStateImpl(
    initialPictureUri: Uri? = null,
    initialDisplayName: String? = null,
    initialPhoneNumbers: List<String> = emptyList(),
    initialSelectedPhoneNumber: String? = null
) : ContactConfigState {

    private var _pictureUri by mutableStateOf(initialPictureUri)
    private var _displayName by mutableStateOf(initialDisplayName)
    private var _phoneNumbers by mutableStateOf(initialPhoneNumbers)
    private var _selectedPhoneNumber by mutableStateOf(initialSelectedPhoneNumber)

    override var pictureUri: Uri?
        get() = _pictureUri
        set(value) {
            _pictureUri = value
        }

    override var displayName: String?
        get() = _displayName
        set(value) {
            _displayName = value
        }

    override var phoneNumbers: List<String>
        get() = _phoneNumbers
        set(value) {
            _phoneNumbers = value
        }

    override var selectedPhoneNumber: String?
        get() = _selectedPhoneNumber
        set(value) {
            _selectedPhoneNumber = value
        }

    companion object {

        val saver by lazy {
            listSaver(
                save = { state ->
                    listOf(
                        state.pictureUri,
                        state.displayName,
                        state.phoneNumbers,
                        state.selectedPhoneNumber
                    )
                },
                restore = { list ->
                    ContactConfigStateImpl(
                        initialPictureUri = list[0] as Uri?,
                        initialDisplayName = list[1] as String?,
                        initialPhoneNumbers = list[2] as List<String>,
                        initialSelectedPhoneNumber = list[3] as String?
                    )
                }
            )
        }
    }
}

@Composable
fun rememberContactConfigState(): ContactConfigState {
    return rememberSaveable(saver = ContactConfigStateImpl.saver) {
        ContactConfigStateImpl()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactConfigScreen(
    title: String,
    state: ContactConfigState = rememberContactConfigState(),
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit = {},
    onPickContactButtonClicked: () -> Unit = {},
    onSaveButtonClicked: () -> Unit = {}
) {
    DirectCallWidgetTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = onCancelButtonClicked) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onPickContactButtonClicked) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_person_search_24),
                                contentDescription = null
                            )
                        }
                        Button(
                            modifier = Modifier.padding(end = 16.dp),
                            onClick = onSaveButtonClicked
                        ) {
                            Text("Save")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(64.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ConfigImage(
                    modifier = Modifier.padding(paddingValues),
                    imageUri = state.pictureUri?.toString(),
                    onImageUriChanged = { uri ->
                        state.pictureUri = uri
                    }
                )
                Spacer(Modifier.height(32.dp))
                ConfigDetails(
                    displayName = state.displayName.orEmpty(),
                    phoneNumbers = emptyList(),
                    selectedPhoneNumber = state.selectedPhoneNumber.orEmpty(),
                    onDisplayNameChanged = { name ->
                        state.displayName = name
                    },
                    onPhoneNumberChanged = { phoneNumber ->
                        state.selectedPhoneNumber = phoneNumber
                    }
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ContactConfigScreenPreview() {
    DirectCallWidgetTheme {
        Scaffold { paddingValues ->
            ContactConfigScreen(
                title = "Contact configuration",
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}