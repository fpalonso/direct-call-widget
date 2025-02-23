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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@Composable
fun ContactConfigScreen(
    uiState: ContactConfigUiState,
    modifier: Modifier = Modifier,
    onImageUriChanged: (Uri?) -> Unit = {},
    onDisplayNameChanged: (String) -> Unit = {},
    onPhoneNumberChanged: (String) -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConfigImage(
                modifier = Modifier.padding(paddingValues),
                imageUri = uiState.imageUri?.toString(),
                onImageUriChanged = onImageUriChanged
            )
            Spacer(Modifier.height(64.dp))
            ConfigDetails(
                displayName = uiState.displayName,
                phoneNumbers = emptyList(),
                selectedPhoneNumber = uiState.selectedPhoneNumber,
                onDisplayNameChanged = onDisplayNameChanged,
                onPhoneNumberChanged = onPhoneNumberChanged
            )
        }
    }
}

@Preview
@Composable
fun ContactConfigScreenPreview() {
    ContactConfigScreen(
        ContactConfigUiState(
            imageUri = "content://Alice.jpg".toUri()
        )
    )
}