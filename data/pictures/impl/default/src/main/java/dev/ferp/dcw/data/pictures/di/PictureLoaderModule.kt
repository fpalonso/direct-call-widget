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

package dev.ferp.dcw.data.pictures.di

import android.graphics.Bitmap
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ferp.dcw.data.pictures.source.disk.BitmapLoader
import dev.ferp.dcw.data.pictures.source.disk.PictureLoader
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PictureLoaderModule {

    @Singleton
    @Binds
    abstract fun bindPictureLoader(
        pictureLoader: BitmapLoader
    ): PictureLoader<Bitmap>
}