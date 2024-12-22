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

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.data.ContactConfig
import com.blaxsoftware.directcallwidget.ui.theme.DirectCallWidgetTheme

@Composable
fun ContactConfigScreen(
    onOkButtonClick: (contactConfig: ContactConfig) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactConfigViewModel = hiltViewModel()
) {
    ContactConfigScreen(
        modifier = modifier,
        pictureUri = viewModel.uiState.pictureUri,
        name = viewModel.uiState.displayName,
        phone = viewModel.uiState.phoneNumber,
        onSearchButtonClick = {},
        onNameChanged = { viewModel.onDisplayNameChanged(it) },
        onPhoneChanged = { viewModel.onPhoneNumberChanged(it) },
        onOkButtonClick = { onOkButtonClick(viewModel.uiState.contactConfig) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactConfigScreen(
    pictureUri: Uri?,
    name: String,
    phone: String,
    onSearchButtonClick: () -> Unit,
    onNameChanged: (name: String) -> Unit,
    onPhoneChanged: (phone: String) -> Unit,
    onOkButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DirectCallWidgetTheme {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(title = {
                    Text(text = stringResource(id = R.string.activity_config_title))
                }, actions = {
                    /*IconButton(onClick = onSearchButtonClick) {
                        Icon(
                            Icons.Rounded.PersonSearch,
                            contentDescription = stringResource(id = R.string.add_contact)
                        )
                    }*/
                })
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.save))
                    },
                    onClick = onOkButtonClick
                )
            }
        ) { padding ->
            BoxWithConstraints {
                if (maxWidth < 600.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ContactPicture(modifier = Modifier.align(Alignment.CenterHorizontally))
                        ContactDetails(
                            name = name,
                            phone = phone,
                            onNameChanged = onNameChanged,
                            onPhoneChanged = onPhoneChanged
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ContactPicture(modifier = Modifier.sizeIn(maxWidth = 320.dp))
                        ContactDetails(
                            modifier = Modifier.fillMaxHeight(),
                            name = name,
                            phone = phone,
                            onNameChanged = onNameChanged,
                            onPhoneChanged = onPhoneChanged
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactPicture(
    modifier: Modifier = Modifier
) {/*
    DcwVerticalPlaceholder(
        modifier = modifier
            .fillMaxWidth(0.5f)
            .padding(bottom = 16.dp),
        icon = Icons.Rounded.InsertPhoto,
        text = stringResource(R.string.add_picture)
    )*/
}

@Composable
private fun ContactDetails(
    name: String,
    phone: String,
    onNameChanged: (name: String) -> Unit,
    onPhoneChanged: (phone: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null
                )
            },
            value = name,
            onValueChange = onNameChanged
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    Icons.Rounded.Phone,
                    contentDescription = null
                )
            },
            value = phone,
            onValueChange = onPhoneChanged
        )
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Contact config Light"
)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Contact config Dark"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Contact config Light (ES)",
    locale = "es",
    widthDp = 600,
    heightDp = 480
)
@Composable
fun ContactConfigScreenPreview() {
    ContactConfigScreen(
        pictureUri = null,
        name = "Fer",
        phone = "12345",
        onSearchButtonClick = {},
        onNameChanged = {},
        onPhoneChanged = {},
        onOkButtonClick = {}
    )
}