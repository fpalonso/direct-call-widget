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

package dev.ferp.dcw.data.pictures.data.source.disk

import android.net.Uri
import androidx.annotation.DrawableRes
import java.io.IOException

/**
 * Implement this interface for loading images.
 *
 * @param T type of the image to load, e.g. Bitmap
 */
interface PictureLoader<T> {

    /**
     * Loads a bitmap from the given [uri].
     *
     * @param uri uri to load the image from
     * @param widthPx width of the resulting image, in pixels
     * @param heightPx height of the resulting image, in pixels
     * @param placeholderId id of the placeholder to set when the operation fails
     * @throws IOException if the operation fails and the placeholder cannot be used
     */
    @Throws(IOException::class)
    suspend fun loadPicture(
        uri: Uri,
        widthPx: Int,
        heightPx: Int,
        @DrawableRes placeholderId: Int
    ): T
}