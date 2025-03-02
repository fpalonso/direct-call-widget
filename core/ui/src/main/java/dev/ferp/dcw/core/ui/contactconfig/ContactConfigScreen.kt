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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.ferp.dcw.core.ui.theme.DirectCallWidgetTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactConfigScreen(
    uiState: ContactConfigUiState,
    modifier: Modifier = Modifier,
    onImageUriChanged: (Uri?) -> Unit = {},
    onDisplayNameChanged: (String) -> Unit = {},
    onPhoneNumberChanged: (String) -> Unit = {},
) {
    DirectCallWidgetTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Contact Configuration") }
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
                    imageUri = uiState.imageUri?.toString(),
                    onImageUriChanged = onImageUriChanged
                )
                Spacer(Modifier.height(32.dp))
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
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ContactConfigScreenPreview() {
    ContactConfigScreen(
        ContactConfigUiState(
            imageUri = "content://Alice.jpg".toUri()
        )
    )
}