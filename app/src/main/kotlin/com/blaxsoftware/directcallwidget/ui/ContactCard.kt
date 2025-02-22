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

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.ui.components.Picture
import dev.ferp.dcw.core.ui.theme.PortraitCardStyle

@Composable
fun ContactCard(
    displayName: String,
    modifier: Modifier = Modifier,
    pictureUri: Uri? = null
) {
    Card(modifier) {
        Box(Modifier.fillMaxSize()) {
            // TODO add placeholder
            Picture(
                modifier = Modifier.fillMaxSize(),
                pictureUri = pictureUri
            )
            ContactCardLabel(text = displayName)
        }
    }
}

@Composable
private fun ContactCardLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black)
                    )
                )
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White,
            textAlign = TextAlign.Start,
            text = text
        )
    }
}

@Preview(name = "Contact card")
@Composable
fun ContactCardPreview() {
    ContactCard(
        modifier = Modifier
            .width(dimensionResource(R.dimen.portrait_card_width))
            .aspectRatio(PortraitCardStyle.WidthRatio),
        displayName = "Contact"
    )
}