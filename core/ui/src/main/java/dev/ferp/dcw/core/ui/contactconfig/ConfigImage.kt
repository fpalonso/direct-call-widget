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

package dev.ferp.dcw.core.ui.contactconfig

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.ferp.dcw.core.ui.R

@Composable
internal fun ConfigImage(
    modifier: Modifier = Modifier,
    imageUri: String? = null,
    onImageUriChanged: (Uri?) -> Unit = {},
) {
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { mediaUri ->
        mediaUri?.let(onImageUriChanged)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .background(Color.Gray, CircleShape)
                .size(128.dp),
            onClick = { pickMedia.launch(PickVisualMediaRequest(ImageOnly)) }
        ) {
            if (imageUri != null) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(R.drawable.baseline_add_photo_alternate_24),
                    contentDescription = null
                )
            }
        }
        Row {
            TextButton(onClick = { pickMedia.launch(PickVisualMediaRequest(ImageOnly)) }) {
                Text(stringResource(R.string.pick_image))
            }
            if (imageUri != null) {
                TextButton(onClick = { onImageUriChanged(null) }) {
                    Text(stringResource(R.string.clear_image))
                }
            }
        }
    }
}

@Preview
@Composable
fun EmptyConfigImagePreview() {
    ConfigImage(Modifier.fillMaxSize())
}

@Preview
@Composable
fun SetConfigImagePreview() {
    ConfigImage(
        Modifier.fillMaxSize(),
        imageUri = "content://fancyimage.jpg"
    )
}