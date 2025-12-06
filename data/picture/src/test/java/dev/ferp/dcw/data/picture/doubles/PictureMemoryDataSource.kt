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

package dev.ferp.dcw.data.picture.doubles

import android.graphics.Bitmap
import dev.ferp.dcw.data.picture.mother.BitmapMother
import dev.ferp.dcw.data.picture.source.PictureDataSource

class PictureMemoryDataSource : PictureDataSource {

    private val pictures = mutableMapOf<String, Bitmap>()

    override suspend fun addPicture(pictureUri: String): Result<String> {
        val internalUri = "${pictureUri}_internal"
        pictures[internalUri] = BitmapMother.bitmap()
        return Result.success(internalUri)
    }

    override suspend fun getPicture(
        pictureUri: String,
        widthPx: Int,
        heightPx: Int,
        placeholder: Int?
    ): Result<Bitmap> {
        return pictures[pictureUri]?.let { bitmap ->
            Result.success(bitmap)
        } ?: Result.failure(IllegalArgumentException("Picture not found"))
    }

    override suspend fun deletePicture(pictureUri: String): Boolean {
        val result = pictures.contains(pictureUri)
        pictures.remove(pictureUri)
        return result
    }
}