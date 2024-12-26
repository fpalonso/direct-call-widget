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
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
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
import com.blaxsoftware.directcallwidget.data.ContactConfig
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
                            GlanceContactCard(
                                modifier = GlanceModifier
                                    // TODO extract dimensions
                                    .size(width = 130.dp, height = 175.dp),
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
    Box(modifier
        .fillMaxWidth()
        .height(175.dp) // TODO Extract
        .cornerRadius(16.dp)
    ) {
        Image(
            modifier = GlanceModifier.fillMaxSize(),
            provider = getImageProvider(contactConfig.pictureUri),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        GlanceContactCardLabel(contactConfig.displayName)
    }
}

private fun getImageProvider(path: String): ImageProvider {
    if (path.startsWith("content://")) {
        return ImageProvider(path.toUri())
    }
    val bitmap = BitmapFactory.decodeFile(path)
    return ImageProvider(bitmap)
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