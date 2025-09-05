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

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ferp.dcw.core.ui.theme.DirectCallWidgetTheme

/**
 * State holder with information about a contact that will be added to a widget.
 */
@Stable
interface ContactConfigState {
    var pictureUri: String?
    var displayName: String?
    var selectedPhoneNumber: String?
}

/**
 * Saveable implementation of [ContactConfigState].
 */
@Stable
private class SaveableContactConfigState(
    initialPictureUri: String? = null,
    initialDisplayName: String? = null,
    initialSelectedPhoneNumber: String? = null
) : ContactConfigState {

    private var _pictureUri by mutableStateOf(initialPictureUri)
    private var _displayName by mutableStateOf(initialDisplayName)
    private var _selectedPhoneNumber by mutableStateOf(initialSelectedPhoneNumber)

    override var pictureUri: String?
        get() = _pictureUri
        set(value) {
            _pictureUri = value
        }

    override var displayName: String?
        get() = _displayName
        set(value) {
            _displayName = value
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
                        state.selectedPhoneNumber
                    )
                },
                restore = { list ->
                    SaveableContactConfigState(
                        initialPictureUri = list[0],
                        initialDisplayName = list[1],
                        initialSelectedPhoneNumber = list[2]
                    )
                }
            )
        }
    }
}

@Composable
fun rememberContactConfigState(): ContactConfigState {
    return rememberSaveable(saver = SaveableContactConfigState.saver) {
        SaveableContactConfigState()
    }
}

@Composable
fun ContactConfigScreen(
    modifier: Modifier = Modifier,
    state: ContactConfigState = rememberContactConfigState(),
    viewModel: ContactConfigViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Contact picker
    val contactPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { pickedContactUri ->
        viewModel.onPickedContact(pickedContactUri)
    }

    // Permission request
    val readContactsPermission = Manifest.permission.READ_CONTACTS
    val permissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contactPicker.launch(null)
        }
    }

    // Rationale
    var showRationale by rememberSaveable { mutableStateOf(false) }
    if (showRationale) {
        AlertDialog(
            text = {
                Text(stringResource(R.string.read_contacts_rationale))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        permissionRequest.launch(readContactsPermission)
                    }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {},
            onDismissRequest = { showRationale = false }
        )
    }
    val activity = LocalActivity.current as Activity

    fun launchContactPicker() {
        when {
            // Permission is granted
            ContextCompat.checkSelfPermission(
                activity, readContactsPermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                contactPicker.launch(null)
            }

            // Should show permission request rationale
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_CONTACTS
            ) -> showRationale = true

            // Request permission directly
            else -> permissionRequest.launch(readContactsPermission)
        }
    }

    ContactConfigContent(
        modifier = modifier,
        title = "",
        state = uiState,
        onPictureChanged = {
            viewModel.onPictureChanged(it)
        },
        onDisplayNameChanged = {
            viewModel.onDisplayNameChanged(it)
        },
        onPhoneNumberChanged = {
            viewModel.onPhoneNumberChanged(it)
        },
        onPickContactButtonClick = {
            launchContactPicker()
        },
        onNavigationIconClick = onDismiss,
        onSaveButtonClick = {
            state.pictureUri = uiState.pictureUri
            state.displayName = uiState.displayName
            state.selectedPhoneNumber = uiState.phoneNumber
            onSave()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactConfigContent(
    title: String,
    modifier: Modifier = Modifier,
    state: InternalContactConfigUiState = InternalContactConfigUiState(),
    onPictureChanged: (String?) -> Unit = {},
    onDisplayNameChanged: (String) -> Unit = {},
    onPhoneNumberChanged: (String) -> Unit = {},
    onPickContactButtonClick: () -> Unit = {},
    onNavigationIconClick: () -> Unit = {},
    onSaveButtonClick: () -> Unit = {}
) {
    val orientation = LocalConfiguration.current.orientation
    DirectCallWidgetTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = onNavigationIconClick) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        Button(
                            modifier = Modifier.padding(end = 16.dp),
                            onClick = onSaveButtonClick
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                )
            },
            floatingActionButton = {
                LargeFloatingActionButton(
                    onClick = onPickContactButtonClick
                ) {
                    Icon(
                        imageVector = ImageVector
                            .vectorResource(R.drawable.baseline_person_search_24),
                        contentDescription = null
                    )
                }
            }
        ) { paddingValues ->
            when (orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    LandscapeContent(
                        modifier = Modifier.padding(paddingValues),
                        pictureUri = state.pictureUri,
                        displayName = state.displayName,
                        phoneNumber = state.phoneNumber,
                        onPictureChanged = onPictureChanged,
                        onDisplayNameChanged = onDisplayNameChanged,
                        onPhoneNumberChanged = onPhoneNumberChanged
                    )
                }
                else -> {
                    PortraitContent(
                        modifier = Modifier.padding(paddingValues),
                        pictureUri = state.pictureUri,
                        displayName = state.displayName,
                        phoneNumber = state.phoneNumber,
                        onPictureChanged = onPictureChanged,
                        onDisplayNameChanged = onDisplayNameChanged,
                        onPhoneNumberChanged = onPhoneNumberChanged
                    )
                }
            }
        }
    }
}

@Composable
private fun PortraitContent(
    pictureUri: String?,
    displayName: String,
    phoneNumber: String,
    modifier: Modifier = Modifier,
    onPictureChanged: (String?) -> Unit = {},
    onDisplayNameChanged: (String) -> Unit = {},
    onPhoneNumberChanged: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(64.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContactPicture(
            pictureUri = pictureUri,
            onPictureUriChanged = { uri ->
                onPictureChanged(uri?.toString())
            }
        )
        Spacer(Modifier.height(32.dp))
        ContactDetails(
            displayName = displayName,
            phoneNumbers = emptyList(),
            selectedPhoneNumber = phoneNumber,
            onDisplayNameChanged = onDisplayNameChanged,
            onPhoneNumberChanged = onPhoneNumberChanged
        )
    }
}

@Composable
private fun LandscapeContent(
    pictureUri: String?,
    displayName: String,
    phoneNumber: String,
    modifier: Modifier = Modifier,
    onPictureChanged: (String?) -> Unit = {},
    onDisplayNameChanged: (String) -> Unit = {},
    onPhoneNumberChanged: (String) -> Unit = {}
) {
    Row(modifier
        .fillMaxSize()
        .padding(64.dp)
        .verticalScroll(rememberScrollState())
    ) {
        ContactPicture(
            pictureUri = pictureUri,
            onPictureUriChanged = { uri ->
                onPictureChanged(uri?.toString())
            }
        )
        Spacer(Modifier.width(64.dp))
        ContactDetails(
            displayName = displayName,
            phoneNumbers = emptyList(),
            selectedPhoneNumber = phoneNumber,
            onDisplayNameChanged = onDisplayNameChanged,
            onPhoneNumberChanged = onPhoneNumberChanged
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(widthDp = 915, heightDp = 412)
@Composable
private fun ContactConfigScreenPreview() {
    DirectCallWidgetTheme {
        Scaffold { paddingValues ->
            ContactConfigContent(
                title = "Contact configuration",
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}