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
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
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
import com.blaxsoftware.directcallwidget.Caller
import com.blaxsoftware.directcallwidget.Dialer
import com.blaxsoftware.directcallwidget.Preferences
import com.blaxsoftware.directcallwidget.Preferences.WidgetClickAction
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.asPhoneUri
import com.blaxsoftware.directcallwidget.data.ContactConfig
import com.blaxsoftware.directcallwidget.data.MultiContactInfo
import com.blaxsoftware.directcallwidget.ui.MultiContactAppWidget.ActionParameterKeys.phoneNumberKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class MultiContactAppWidget : GlanceAppWidget() {

    override val stateDefinition = MultiContactStateDefinition

    object ActionParameterKeys {
        val phoneNumberKey = ActionParameters.Key<String>("phoneNumber")
    }

    object Sizes {
        internal val SMALL_SQUARE = DpSize(40.dp, 110.dp)
        internal val BIG_SQUARE = DpSize(180.dp, 270.dp)
        internal val MEDIUM_SQUARE = DpSize(110.dp, 165.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        sizes = setOf(
            Sizes.SMALL_SQUARE,
            Sizes.MEDIUM_SQUARE,
            Sizes.BIG_SQUARE
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
                                contactConfig = contact,
                                cardClickAction = actionRunCallback<HandleWidgetClickAction>(
                                    actionParametersOf(
                                        phoneNumberKey to contact.phoneNumber
                                    )
                                )
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

class HandleWidgetClickAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HandleWidgetClickActionEntryPoint {
        fun preferences(): Preferences
        fun dialer(): Dialer
        fun caller(): Caller
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val phoneUri = parameters[phoneNumberKey]?.asPhoneUri() ?: return
        val hiltEntryPoint = EntryPointAccessors
            .fromApplication(context, HandleWidgetClickActionEntryPoint::class.java)
        val prefs = hiltEntryPoint.preferences()
        if (prefs.getWidgetClickAction() == WidgetClickAction.DIAL) {
            hiltEntryPoint.dialer().dial(phoneUri)
        } else {
            hiltEntryPoint.caller().call(phoneUri)
        }
    }
}

@Composable
private fun GlanceContactCard(
    contactConfig: ContactConfig,
    modifier: GlanceModifier = GlanceModifier,
    cardClickAction: Action = action {  }
) {
    Column(modifier.padding(4.dp)) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(16.dp)
                .clickable(cardClickAction)
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