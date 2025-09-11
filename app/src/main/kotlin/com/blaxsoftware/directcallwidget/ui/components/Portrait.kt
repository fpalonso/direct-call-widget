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

package com.blaxsoftware.directcallwidget.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.InsertPhoto
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.blaxsoftware.directcallwidget.R
import dev.ferp.dcw.core.ui.theme.PortraitCardStyle

@Composable
fun PickablePicture(
    modifier: Modifier = Modifier,
    pictureUri: Uri? = null,
    onUriChanged: (Uri?) -> Unit = {}
) {
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        onUriChanged(uri)
    }
    Card(
        modifier = modifier,
        onClick = { pickMedia.launch(PickVisualMediaRequest(ImageOnly)) }
    ) {
        if (pictureUri == null) {
            PicturePlaceholder(
                modifier = Modifier.fillMaxSize(),
                icon = Icons.Rounded.InsertPhoto,
                text = stringResource(R.string.add_picture)
            )
        } else {
            Picture(
                modifier = Modifier.fillMaxSize(),
                pictureUri = pictureUri
            )
        }
    }
}

@Composable
fun PicturePlaceholder(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    text: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = icon,
            contentDescription = null
        )
        text?.let { Text(it) }
    }
}

@Composable
fun Picture(
    pictureUri: Uri?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        modifier = modifier,
        contentScale = ContentScale.Crop,
        model = pictureUri,
        contentDescription = null // TODO
    )
}

@Preview(name = "Placeholder")
@Composable
fun PickablePlaceholderPreview() {
    PickablePicture(
        modifier = Modifier
            .width(dimensionResource(R.dimen.portrait_card_width))
            .aspectRatio(PortraitCardStyle.WidthRatio)
    )
}
