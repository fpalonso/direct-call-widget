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

package dev.ferp.dcw.data.pictures

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import java.util.UUID

class FakeWidgetPictureRepository : WidgetPictureRepository<Uri, Uri, Bitmap, Int> {

    private val pictures = mutableMapOf<Uri, Bitmap>()

    override suspend fun addPicture(source: Uri): Result<Uri> {
        val internalUri = "content://pictures/${UUID.randomUUID()}".toUri()
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        pictures[internalUri] = bitmap
        return Result.success(internalUri)
    }

    override suspend fun getPicture(
        locator: Uri,
        widthPx: Int,
        heightPx: Int,
        placeholder: Int?
    ): Result<Bitmap> {
        return pictures[locator]?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Picture not found"))
    }

    override suspend fun deletePicture(locator: Uri): Boolean {
        val result = pictures.containsKey(locator)
        pictures.remove(locator)
        return result
    }
}