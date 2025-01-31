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
 */
interface PictureRepository<S, L, V> {

    /**
     * Adds the given picture to the repository.
     *
     * @param source the locator for the source picture
     * @return the repository picture locator
     * @throws IOException if the operation fails
     */
    @Throws(IOException::class)
    suspend fun addPicture(source: S): L

    /**
     * Returns the picture from its locator.
     *
     * @param locator the locator for the picture to get
     * @return the picture, or null if it was not found
     * @throws IOException if the operation fails
     */
    @Throws(IOException::class)
    suspend fun getPicture(locator: L): V?

    /**
     * Deletes the given picture from the repository.
     *
     * @param locator the locator for the source picture
     * @return true if the file could be deleted, false if it didn't exist
     * @throws IOException if the operation fails
     */
    @Throws(IOException::class)
    suspend fun deletePicture(locator: L): Boolean
}