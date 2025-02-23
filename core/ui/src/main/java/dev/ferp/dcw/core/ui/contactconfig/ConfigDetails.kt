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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ferp.dcw.core.ui.R

@Composable
fun ConfigDetails(
    displayName: String,
    phoneNumbers: List<String>,
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
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.phone_number)) },
            value = selectedPhoneNumber,
            onValueChange = onPhoneNumberChanged
        )
    }
}

@Preview
@Composable
fun ConfigDetailsPreview() {
    ConfigDetails(
        displayName = "Alice",
        phoneNumbers = listOf("123", "456"),
        selectedPhoneNumber = "123"
    )
}