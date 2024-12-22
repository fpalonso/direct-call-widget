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

package com.blaxsoftware.directcallwidget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blaxsoftware.directcallwidget.ui.multicontact.config.MultiContactConfigViewModel
import com.blaxsoftware.directcallwidget.ui.multicontact.config.MultiContactWidgetConfigScreen
import com.blaxsoftware.directcallwidget.ui.singlecontact.config.ContactConfigScreen

@Composable
fun MultiContactNavGraph(
    widgetId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: MultiContactConfig = MultiContactConfig,
    multiContactConfigViewModel: MultiContactConfigViewModel = hiltViewModel(),
    onSaveClick: (widgetId: Int) -> Unit = {}
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<MultiContactConfig> {
            MultiContactWidgetConfigScreen(
                contacts = multiContactConfigViewModel.uiState.contacts,
                onAddContactClick = { navController.navigate(SingleContactConfig) },
                onSaveClick = { onSaveClick(widgetId) }
            )
        }

        composable<SingleContactConfig> {
            ContactConfigScreen(
                onOkButtonClick = { contactConfig ->
                    multiContactConfigViewModel.addContact(contactConfig)
                    navController.popBackStack(MultiContactConfig, inclusive = false)
                }
            )
        }
    }
}