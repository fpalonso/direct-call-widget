/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2024 Fer P. A.
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

package com.blaxsoftware.directcallwidget.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.preference.PreferenceManager
import com.blaxsoftware.directcallwidget.data.source.DefaultContactRepository
import com.blaxsoftware.directcallwidget.data.source.DefaultSingleContactWidgetRepository
import com.blaxsoftware.directcallwidget.data.source.SingleContactWidgetRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ferp.dcw.data.contacts.ContactRepository
import dev.ferp.dcw.data.phones.DevicePhoneRepository
import dev.ferp.dcw.data.phones.PhoneRepository
import dev.ferp.dcw.data.pictures.DefaultWidgetPictureRepository
import dev.ferp.dcw.data.pictures.WidgetPictureRepository
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PicturesDir

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LegacyWidgetInfo

@Module
@InstallIn(SingletonComponent::class)
object FilesModule {

    @Singleton
    @Provides
    fun provideContentResolver(
        @ApplicationContext appContext: Context
    ): ContentResolver = appContext.contentResolver

    @PicturesDir
    @Singleton
    @Provides
    fun providePicturesDir(
        @ApplicationContext appContext: Context
    ) = File(appContext.filesDir, "pics")

    @LegacyWidgetInfo
    @Singleton
    @Provides
    fun provideLegacyWidgetInfoPreferences(
        @ApplicationContext appContext: Context
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Singleton
    @Binds
    abstract fun bindContactRepository(
        contactRepository: DefaultContactRepository
    ): ContactRepository<Uri>

    @Singleton
    @Binds
    abstract fun bindPhoneRepository(
        phoneRepository: DevicePhoneRepository
    ): PhoneRepository

    @Singleton
    @Binds
    abstract fun bindSingleContactWidgetRepository(
        singleContactWidgetRepository: DefaultSingleContactWidgetRepository
    ): SingleContactWidgetRepository

    @Singleton
    @Binds
    abstract fun bindWidgetPictureRepository(
        widgetPictureRepository: DefaultWidgetPictureRepository
    ): WidgetPictureRepository<Uri, Uri, Bitmap, Int>
}