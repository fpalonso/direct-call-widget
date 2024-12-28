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
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.data.ContactConfig
import com.blaxsoftware.directcallwidget.data.MultiContactInfo

class MultiContactAppWidget : GlanceAppWidget() {

    override val stateDefinition = MultiContactStateDefinition

    companion object {
        private val SMALL_SQUARE = DpSize(40.dp, 110.dp)
        private val MEDIUM_SQUARE = DpSize(110.dp, 165.dp)
        private val BIG_SQUARE = DpSize(180.dp, 270.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        sizes = setOf(
            SMALL_SQUARE,
            MEDIUM_SQUARE,
            BIG_SQUARE
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            when (val state = currentState<MultiContactInfo>()) {
                is MultiContactInfo.Available -> {
                    val size = LocalSize.current
                    LazyColumn(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                    ) {
                        items(state.contactList) { contact ->
                            GlanceContactCard(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .height(size.height),
                                contactConfig = contact
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun GlanceContactCard(
    contactConfig: ContactConfig,
    modifier: GlanceModifier = GlanceModifier
) {
    Column(modifier.padding(4.dp)) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(16.dp)
        ) {
            RemoteImage(
                modifier = GlanceModifier.fillMaxSize(),
                model = contactConfig.pictureUri,
                contentDescription = null,
                placeholderResId = R.drawable.ic_default_picture
            )
            GlanceContactCardLabel(contactConfig.displayName)
        }
    }
}

@Composable
private fun GlanceContactCardLabel(
    text: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Column(modifier.fillMaxSize()) {
        Spacer(GlanceModifier.defaultWeight())
        Text(
            modifier = GlanceModifier
                .background(Color(0f, 0f, 0f, 0.5f))
                .fillMaxWidth()
                .padding(16.dp),
            style = TextDefaults
                .defaultTextStyle
                .copy(color = ColorProvider(Color.White)),
            text = text
        )
    }
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 130, heightDp = 175)
@Composable
fun GlanceContactCardPreview() {
    GlanceContactCard(
        modifier = GlanceModifier
            .size(width = 130.dp, height = 175.dp),
        contactConfig = ContactConfig(displayName = "Contact")
    )
}