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

import androidx.navigation.NavHostController
import com.blaxsoftware.directcallwidget.MultiContactDestinationArgs.WIDGET_ID_ARG
import com.blaxsoftware.directcallwidget.MultiContactScreens.CONTACT_CONFIG_SCREEN
import com.blaxsoftware.directcallwidget.MultiContactScreens.MULTICONTACT_CONFIG_SCREEN

private object MultiContactScreens {
    const val MULTICONTACT_CONFIG_SCREEN = "multiContactConfig"
    const val CONTACT_CONFIG_SCREEN = "contactConfig"
}

object MultiContactDestinationArgs {
    const val WIDGET_ID_ARG = "widgetId"
}

object MultiContactDestinations {
    const val MULTICONTACT_CONFIG_ROUTE = "$MULTICONTACT_CONFIG_SCREEN/{$WIDGET_ID_ARG}"
    const val CONTACT_CONFIG_ROUTE = "$CONTACT_CONFIG_SCREEN/{$WIDGET_ID_ARG}"
}

class MultiContactNavigationActions(private val navHostController: NavHostController) {

    fun navigateToContactConfigScreen(widgetId: Int) {
        navHostController.navigate("$CONTACT_CONFIG_SCREEN/$widgetId")
    }
}