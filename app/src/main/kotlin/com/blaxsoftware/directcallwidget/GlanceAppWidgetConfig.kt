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

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.GlanceStateDefinition
import kotlinx.coroutines.launch

/**
 * Type alias for a lambda that will update the widget instance.
 */
typealias UpdateWidget = () -> Unit

/**
 * Container for a Glance app widget configuration screen, which provides the parent activity and
 * the GlanceId of the widget under configuration.
 *
 * @param widgetInstance Any widget instance
 * @param provideState A lambda that resolves the state that will be applied to the app widget
 * @param content The composable to render within this container
 */
@Composable
fun <T : GlanceAppWidget, S> GlanceAppWidgetConfig(
    widgetInstance: T,
    stateDefinition: GlanceStateDefinition<S>,
    provideState: suspend () -> S,
    content: @Composable (Activity, GlanceId, UpdateWidget) -> Unit
) {
    val activity = LocalActivity.current
    val glanceId = remember { activity?.getGlanceId() }
    if (activity == null || glanceId == null) {
        activity?.finish()
        return
    }
    val scope = rememberCoroutineScope()

    content(activity, glanceId) {
        scope.launch {
            updateAppWidgetState(
                context = activity,
                definition = stateDefinition,
                glanceId = glanceId,
                updateState = { provideState() }
            )
            widgetInstance.update(activity, glanceId)
        }
    }
}

fun Activity.getGlanceId() = GlanceAppWidgetManager(this).getGlanceIdBy(intent)