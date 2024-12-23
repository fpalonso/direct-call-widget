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

package com.blaxsoftware.directcallwidget.ui

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.blaxsoftware.directcallwidget.data.MultiContactInfo

class MultiContactAppWidget : GlanceAppWidget() {

    override val stateDefinition = MultiContactStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            when (val state = currentState<MultiContactInfo>()) {
                is MultiContactInfo.Available -> {
                    LazyColumn(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        items(state.contactList) { contact ->
                            Column(
                                GlanceModifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Text(
                                    modifier = GlanceModifier.padding(8.dp),
                                    text = contact.displayName
                                )
                                Text(
                                    modifier = GlanceModifier.padding(8.dp),
                                    text = contact.phoneNumber
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
