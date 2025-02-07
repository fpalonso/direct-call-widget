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

import java.io.IOException

/**
 * Interface for the repository of pictures to be used in app widgets.
 *
 * @param S type for the source picture locator, e.g. Uri
 * @param L type for the repository picture locator, e.g. String
 * @param V type for the picture binary, e.g. Bitmap
 * @param R type for the placeholder resource
 */
interface WidgetPictureRepository<S, L, V, R> {

    /**
     * Adds the given picture to the repository.
     *
     * @param source the locator for the source picture
     * @return the repository picture locator
     * @throws IOException if the operation fails
     */
    suspend fun addPicture(source: S): Result<L>

    /**
     * Returns the picture from its locator, or the placeholder if the main image cannot be loaded.
     *
     * The main image will be centered and cropped by the passed in dimensions.
     *
     * @param locator the locator for the picture to get
     * @param widthPx width of the image to load, in pixels
     * @param heightPx height of the image to load, in pixels
     * @param placeholder resource to load when the picture fails to be loaded
     * @return the picture
     * @throws IOException if neither the picture nor the placeholder could be loaded
     */
    suspend fun getPicture(
        locator: L,
        widthPx: Int,
        heightPx: Int,
        placeholder: R? = null
    ): Result<V>

    /**
     * Deletes the given picture from the repository.
     *
     * @param locator the locator for the source picture
     * @return true if the file could be deleted, false if it didn't exist
     * @throws IOException if the operation fails
     */
    suspend fun deletePicture(locator: L): Boolean
}