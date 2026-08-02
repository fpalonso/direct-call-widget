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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blaxsoftware.directcallwidget.GlanceAppWidgetConfig
import com.blaxsoftware.directcallwidget.data.ContactConfig
import com.blaxsoftware.directcallwidget.ui.MultiContactAppWidget
import com.blaxsoftware.directcallwidget.ui.MultiContactStateDefinition
import dev.ferp.dcw.feature.contactconfig.ContactConfigScreen

@Composable
fun MultiContactNavGraph(
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = MultiContactConfig,
    multiContactConfigViewModel: MultiContactConfigViewModel = hiltViewModel()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<MultiContactConfig> {
            GlanceAppWidgetConfig(
                widgetInstance = MultiContactAppWidget(),
                stateDefinition = MultiContactStateDefinition,
                provideState = { multiContactConfigViewModel.buildWidgetInfo() }
            ) { _, _, updateWidget ->
                MultiContactWidgetConfigScreen(
                    contacts = multiContactConfigViewModel.uiState.contacts,
                    onAddContactClick = { navController.navigate(SingleContactConfig) },
                    onSaveClick = {
                        updateWidget()
                        onSaveClick()
                    }
                )
            }
        }

        composable<SingleContactConfig> {
            ContactConfigScreen(
                shouldLaunchContactPicker = true,
                onSave = { fieldValues ->
                    multiContactConfigViewModel.addContact(
                        ContactConfig(
                            pictureUri = fieldValues.pictureUri.orEmpty(),
                            displayName = fieldValues.displayName.orEmpty(),
                            phoneNumber = fieldValues.phoneNumber.orEmpty()
                        )
                    )
                    navController.popBackStack()
                },
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}
