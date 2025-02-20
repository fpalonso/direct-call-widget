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

package com.blaxsoftware.directcallwidget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.blaxsoftware.directcallwidget.R
import dev.ferp.dcw.feature.addwidget.WidgetListScreen
import dev.ferp.dcw.feature.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object WidgetList

@Serializable
class Settings(val appVersionName: String, val canNavigateBack: Boolean = false)

@Composable
fun MainNavHost(
    appVersionName: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = WidgetList
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<WidgetList> {
            WidgetListScreen(
                modifier = modifier,
                title = stringResource(R.string.app_name),
                onSettingsActionClick = {
                    navController.navigate(
                        Settings(appVersionName, canNavigateBack = true)
                    )
                }
            )
        }

        composable<Settings> { backStackEntry ->
            val route: Settings = backStackEntry.toRoute()
            SettingsScreen(
                modifier = modifier,
                versionName = route.appVersionName,
                canNavigateBack = route.canNavigateBack,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}