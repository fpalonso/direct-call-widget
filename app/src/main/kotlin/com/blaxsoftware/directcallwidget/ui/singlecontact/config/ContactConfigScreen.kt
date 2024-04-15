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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.ui.theme.DirectCallWidgetTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactConfigScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactConfigViewModel = hiltViewModel()
) {
    DirectCallWidgetTheme {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(title = {
                    Text(text = stringResource(id = R.string.activity_config_title))
                })
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {

            }
        }
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
@Composable
fun ContactConfigScreenPreview() {
    ContactConfigScreen()
}