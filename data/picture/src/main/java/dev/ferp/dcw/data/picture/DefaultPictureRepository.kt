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

package dev.ferp.dcw.data.picture

import android.graphics.Bitmap
import dev.ferp.dcw.core.domain.data.picture.PictureRepository
import dev.ferp.dcw.data.picture.source.PictureDataSource
import javax.inject.Inject

internal class DefaultPictureRepository @Inject constructor(
    private val dataSource: PictureDataSource
) : PictureRepository<Bitmap> {

    override suspend fun addPicture(pictureUri: String): Result<String> {
        return dataSource.addPicture(pictureUri)
    }

    override suspend fun getPicture(
        pictureUri: String,
        widthPx: Int,
        heightPx: Int,
        placeholder: Int?
    ): Result<Bitmap> = dataSource.getPicture(
        pictureUri,
        widthPx,
        heightPx,
        placeholder
    )

    override suspend fun deletePicture(pictureUri: String): Boolean {
        return dataSource.deletePicture(pictureUri)
    }
}