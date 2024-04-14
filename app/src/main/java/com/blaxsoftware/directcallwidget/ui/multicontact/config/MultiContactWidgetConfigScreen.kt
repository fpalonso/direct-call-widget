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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.ui.multicontact.config.ui.theme.DirectcallwidgetTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiContactWidgetConfigScreen(
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DirectcallwidgetTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(title = {
                    Text(text = stringResource(R.string.multicontact_widget_setup))
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
                    columns = GridCells.Adaptive(minSize = 130.dp)
                ) {
                    item {
                        AddContactItem(
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddContactItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(130.dp)
            .aspectRatio(0.75f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = null
            )
            Text(text = stringResource(R.string.add_contact))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MultiContactWidgetConfigScreenPreview() {
    MultiContactWidgetConfigScreen(onSaveClick = { })
}

@Preview(showBackground = true)
@Composable
private fun AddContactItemPreview() {
    AddContactItem(
        onClick = {}
    )
}