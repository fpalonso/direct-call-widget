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

package dev.ferp.dcw.data.picture.di

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ferp.dcw.data.picture.source.local.BitmapLoader
import dev.ferp.dcw.data.picture.source.local.PictureLoader
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class WidgetPicture

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PictureLoaderModule {

    companion object {
        @WidgetPicture
        @Singleton
        @Provides
        fun provideWidgetPictureGlideRequestBuilder(
            @ApplicationContext context: Context
        ): RequestBuilder<Bitmap> = Glide
            .with(context)
            .asBitmap()

        @WidgetPicture
        @Provides
        fun provideDiskCacheStrategy(): DiskCacheStrategy = DiskCacheStrategy.NONE
    }

    @Binds
    abstract fun bindPictureLoader(
        impl: BitmapLoader
    ): PictureLoader
}