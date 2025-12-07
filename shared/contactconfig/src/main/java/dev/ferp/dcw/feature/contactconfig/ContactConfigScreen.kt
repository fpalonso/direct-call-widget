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
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.ferp.dcw.core.ui.theme.DirectCallWidgetTheme

data class FieldValues(
    val pictureUri: String? = null,
    val displayName: String? = null,
    val phoneNumber: String? = null
)

/**
 * Screen that allows the user to configure a contact to be added to a widget.
 *
 * @param modifier Optional [Modifier] for this screen
 * @param viewModel The [ContactConfigViewModel] that will hold the UI state
 * @param initialFieldValues initial values for the screen fields
 * @param shouldLaunchContactPicker true to launch the contact picker immediately
 * @param onDismiss Callback invoked when the user wants to dismiss the screen
 * @param onSave Callback invoked when the user wants to save the contact configuration
 */
@Suppress("AssignedValueIsNeverRead")
@Composable
fun ContactConfigScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactConfigViewModel = hiltViewModel(),
    initialFieldValues: FieldValues = FieldValues(),
    shouldLaunchContactPicker: Boolean = false,
    onDismiss: () -> Unit = {},
    onSave: (FieldValues) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Set initial values
    LaunchedEffect(Unit) {
        viewModel.onPictureChanged(initialFieldValues.pictureUri)
        viewModel.onDisplayNameChanged(initialFieldValues.displayName.orEmpty())
        viewModel.onPhoneNumberChanged(initialFieldValues.phoneNumber.orEmpty())
    }

    // Contact picker
    val contactPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { pickedContactUri ->
        if (pickedContactUri != null) {
            viewModel.logContactPicked()
        } else {
            viewModel.logContactPickerDismissed()
        }
        viewModel.onPickedContact(pickedContactUri)
    }

    // Permission request
    val readContactsPermission = Manifest.permission.READ_CONTACTS
    val permissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.logContactPermissionGranted()
            viewModel.logContactPickerLaunched()
            contactPicker.launch(null)
        } else {
            viewModel.logContactPermissionDenied()
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
                        showRationale = false
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
                viewModel.logContactPickerLaunched()
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

    LaunchedEffect(shouldLaunchContactPicker) {
        if (shouldLaunchContactPicker) {
            launchContactPicker()
        }
    }

    ContactConfigContent(
        modifier = modifier,
        title = "",
        state = uiState,
        onPicturePickerLaunched = viewModel::logPicturePickerLaunched,
        onPictureChanged = { pictureUri ->
            if (pictureUri != null) {
                viewModel.logPicturePicked()
            } else {
                viewModel.logPicturePickerDismissed()
            }
            viewModel.onPictureChanged(pictureUri)
        },
        onDisplayNameChanged = viewModel::onDisplayNameChanged,
        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onPickContactButtonClick = {
            viewModel.logPickContactClick()
            launchContactPicker()
        },
        onNavigationIconClick = {
            viewModel.logDismiss()
            onDismiss()
        },
        onSaveButtonClick = {
            viewModel.logSave()
            onSave(
                FieldValues(
                    pictureUri = uiState.pictureUri,
                    displayName = uiState.displayName,
                    phoneNumber = uiState.phoneNumber
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactConfigContent(
    title: String,
    modifier: Modifier = Modifier,
    state: InternalContactConfigUiState = InternalContactConfigUiState(),
    onPicturePickerLaunched: () -> Unit = {},
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
                        onPicturePickerLaunched = onPicturePickerLaunched,
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
                        onPicturePickerLaunched = onPicturePickerLaunched,
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
    onPicturePickerLaunched: () -> Unit = {},
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
            onPicturePickerLaunched = onPicturePickerLaunched,
            onPictureUriChanged = { uri ->
                onPictureChanged(uri?.toString())
            }
        )
        Spacer(Modifier.height(32.dp))
        ContactDetails(
            displayName = displayName,
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
    onPicturePickerLaunched: () -> Unit = {},
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
            onPicturePickerLaunched = onPicturePickerLaunched,
            onPictureUriChanged = { uri ->
                onPictureChanged(uri?.toString())
            }
        )
        Spacer(Modifier.width(64.dp))
        ContactDetails(
            displayName = displayName,
            selectedPhoneNumber = phoneNumber,
            onDisplayNameChanged = onDisplayNameChanged,
            onPhoneNumberChanged = onPhoneNumberChanged
        )
    }
}

/**
 * Contact picture that handles the image picker.
 *
 * @param pictureUri The URI of the picture
 * @param onPictureUriChanged Callback to update the picture URI. If the URI is null,
 * it means that the user has chosen to delete the picture. That is, if the image picker
 * is opened but no picture is selected, this callback will not be invoked.
 */
@Composable
private fun ContactPicture(
    modifier: Modifier = Modifier,
    pictureUri: String? = null,
    onPicturePickerLaunched: () -> Unit = {},
    onPictureUriChanged: (Uri?) -> Unit = {},
) {
    val mediaPicker = rememberLauncherForActivityResult(PickVisualMedia()) { mediaUri ->
        // If no new picture has been selected, keep the old one.
        mediaUri?.let(onPictureUriChanged)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .background(color = Color.LightGray, CircleShape)
                .size(128.dp),
            onClick = {
                onPicturePickerLaunched()
                mediaPicker.launch(PickVisualMediaRequest(ImageOnly))
            }
        ) {
            if (pictureUri != null) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = pictureUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(R.drawable.baseline_add_photo_alternate_24),
                    contentDescription = null
                )
            }
        }
        if (pictureUri != null) {
            Row {
                TextButton(onClick = {
                    onPicturePickerLaunched()
                    mediaPicker.launch(PickVisualMediaRequest(ImageOnly))
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                    Text(stringResource(R.string.change_image))
                }
                TextButton(onClick = { onPictureUriChanged(null) }) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null
                    )
                    Text(stringResource(R.string.delete_image))
                }
            }
        } else {
            TextButton(onClick = {
                onPicturePickerLaunched()
                mediaPicker.launch(PickVisualMediaRequest(ImageOnly))
            }) {
                Text(stringResource(R.string.add_image))
            }
        }
    }
}

@Composable
private fun ContactDetails(
    displayName: String,
    selectedPhoneNumber: String,
    modifier: Modifier = Modifier,
    onDisplayNameChanged: (String) -> Unit = {},
    onPhoneNumberChanged: (String) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.display_name)) },
            value = displayName,
            onValueChange = onDisplayNameChanged
        )
        // TODO Display a list of options
        // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#ExposedDropdownMenuBox(kotlin.Boolean,kotlin.Function1,androidx.compose.ui.Modifier,kotlin.Function1)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.phone_number)) },
            value = selectedPhoneNumber,
            onValueChange = onPhoneNumberChanged
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun EmptyContactImagePreview() {
    DirectCallWidgetTheme {
        Surface {
            ContactPicture()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SetContactImagePreview() {
    DirectCallWidgetTheme {
        Surface {
            ContactPicture(
                pictureUri = "content://fancyimage.jpg"
            )
        }
    }
}

@Preview
@Composable
private fun ConfigDetailsPreview() {
    ContactDetails(
        displayName = "Alice",
        selectedPhoneNumber = "123"
    )
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