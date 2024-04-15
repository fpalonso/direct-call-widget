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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blaxsoftware.directcallwidget.ui.multicontact.config.MultiContactWidgetConfigScreen
import com.blaxsoftware.directcallwidget.ui.singlecontact.config.ContactConfigScreen

@Composable
fun MultiContactNavGraph(
    widgetId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MultiContactDestinations.MULTICONTACT_CONFIG_ROUTE,
    navigationActions: MultiContactNavigationActions = remember {
        MultiContactNavigationActions(navController)
    },
    onSaveClick: (widgetId: Int) -> Unit = {}
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        composable(
            route = MultiContactDestinations.MULTICONTACT_CONFIG_ROUTE,
            arguments = listOf(
                navArgument(MultiContactDestinationArgs.WIDGET_ID_ARG) {
                    type = NavType.IntType
                    // https://stackoverflow.com/a/69038771
                    defaultValue = widgetId
                }
            )
        ) {
            MultiContactWidgetConfigScreen(
                onAddContactClick = { navigationActions.navigateToContactConfigScreen(widgetId) },
                onSaveClick = { onSaveClick(widgetId) }
            )
        }

        composable(
            route = MultiContactDestinations.CONTACT_CONFIG_ROUTE,
            arguments = listOf(
                navArgument(MultiContactDestinationArgs.WIDGET_ID_ARG) {
                    type = NavType.IntType
                }
            )
        ) {
            ContactConfigScreen()
        }
    }
}