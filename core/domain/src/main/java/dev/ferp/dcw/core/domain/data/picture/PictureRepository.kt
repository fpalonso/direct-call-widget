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

package dev.ferp.dcw.core.domain.data.picture

interface PictureRepository<ImageType> {
    /**
     * Adds a picture to the repository and returns a [Result] with its internal URI.
     */
    suspend fun addPicture(pictureUri: String): Result<String>

    /**
     * Returns a picture from the internal URI returned by [addPicture].
     */
    suspend fun getPicture(
        pictureUri: String,
        widthPx: Int,
        heightPx: Int,
        placeholder: Int? = null
    ): Result<ImageType>

    /**
     * Deletes the picture with the internal URI returned by [addPicture] from the repository.
     */
    suspend fun deletePicture(pictureUri: String): Boolean
}