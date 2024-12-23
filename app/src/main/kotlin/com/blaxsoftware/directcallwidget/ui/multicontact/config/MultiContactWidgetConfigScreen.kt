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

package com.blaxsoftware.directcallwidget.ui.multicontact.config

import android.app.Activity
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.navigation.compose.hiltViewModel
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.data.ContactConfig
import com.blaxsoftware.directcallwidget.ui.MultiContactAppWidget
import com.blaxsoftware.directcallwidget.ui.MultiContactStateDefinition
import com.blaxsoftware.directcallwidget.ui.components.DcwVerticalPlaceholder
import com.blaxsoftware.directcallwidget.ui.theme.DirectCallWidgetTheme
import com.blaxsoftware.directcallwidget.ui.theme.VerticalPlaceholderStyle
import kotlinx.coroutines.launch

@Composable
fun MultiContactWidgetConfigScreen(
    modifier: Modifier = Modifier,
    viewModel: MultiContactConfigViewModel = hiltViewModel(),
    onAddContactClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    // TODO move to navigation graph and remove viewModel dependency
    val activity = LocalContext.current as Activity
    val glanceAppWidgetManager = GlanceAppWidgetManager(activity)
    val glanceId = remember {
        glanceAppWidgetManager.getGlanceIdBy(activity.intent)
    }
    if (glanceId == null) {
        activity.finish()
        return
    }
    val scope = rememberCoroutineScope()
    MultiContactWidgetConfigScreen(
        modifier,
        viewModel.uiState.contacts,
        onAddContactClick,
        onSaveClick = {
            scope.launch {
                updateAppWidgetState(
                    context = activity,
                    definition = MultiContactStateDefinition,
                    glanceId = glanceId,
                    updateState = { viewModel.buildWidgetInfo() }
                )
                MultiContactAppWidget().update(activity, glanceId)
            }
            onSaveClick()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiContactWidgetConfigScreen(
    modifier: Modifier = Modifier,
    contacts: List<ContactConfig> = emptyList(),
    onAddContactClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    DirectCallWidgetTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(title = {
                    Text(text = stringResource(R.string.title_activity_multi_contact_widget_config))
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
                        Text(text = stringResource(R.string.save))
                    },
                    onClick = onSaveClick
                )
            }
        ) { padding ->
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    columns = GridCells.Adaptive(minSize = 130.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(contacts) { contact ->
                        Contact(
                            pictureUri = contact.pictureUri.toUri(),
                            displayName = contact.displayName,
                            phoneNumber = contact.phoneNumber
                        )
                    }
                    item {
                        DcwVerticalPlaceholder(
                            modifier = modifier
                                .width(130.dp)
                                .aspectRatio(VerticalPlaceholderStyle.WidthRatio),
                            icon = Icons.Rounded.PersonAdd,
                            text = stringResource(id = R.string.add_contact),
                            onClick = onAddContactClick
                        )
                    }
                }
            }
        }
    }
}

// TODO extract to a component
@Composable
fun Contact(
    pictureUri: Uri,
    displayName: String,
    phoneNumber: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(130.dp) // TODO extract styles
            .aspectRatio(VerticalPlaceholderStyle.WidthRatio)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))
            Text(displayName)
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "MultiContact config Light"
)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "MultiContact config Dark"
)
@Composable
private fun MultiContactWidgetConfigScreenPreview() {
    MultiContactWidgetConfigScreen(
        contacts = listOf(
            ContactConfig(
                pictureUri = "",
                displayName = "Mom",
                phoneNumber = "123"
            ),
            ContactConfig(
                pictureUri = "",
                displayName = "Dad",
                phoneNumber = "123"
            )
        )
    )
}