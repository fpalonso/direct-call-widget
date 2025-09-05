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

package com.blaxsoftware.directcallwidget.di

import coil3.Bitmap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ferp.dcw.core.domain.data.devicecontact.DeviceContactRepository
import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidgetRepository
import dev.ferp.dcw.core.domain.data.picture.PictureRepository
import dev.ferp.dcw.core.domain.devicecontact.GetDeviceContactUseCase
import dev.ferp.dcw.core.domain.onecontactwidget.DeleteOneContactWidgetUseCase
import dev.ferp.dcw.core.domain.onecontactwidget.SaveOneContactWidgetUseCase
import dev.ferp.dcw.core.domain.picture.AddPictureUseCase
import dev.ferp.dcw.core.domain.picture.DeletePictureUseCase

// TODO move to :core:domain
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideGetDeviceContactUseCase(
        deviceContactRepository: DeviceContactRepository
    ) = GetDeviceContactUseCase(deviceContactRepository)

    @Provides
    fun provideAddPictureUseCase(
        pictureRepository: PictureRepository<Bitmap>
    ) = AddPictureUseCase(pictureRepository)

    @Provides
    fun provideDeletePictureUseCase(
        pictureRepository: PictureRepository<Bitmap>
    ) = DeletePictureUseCase(pictureRepository)

    @Provides
    fun provideDeleteOneContactWidgetUseCase(
        oneContactWidgetRepository: OneContactWidgetRepository,
        deletePictureUseCase: DeletePictureUseCase<Bitmap>
    ) = DeleteOneContactWidgetUseCase(
        oneContactWidgetRepository,
        deletePictureUseCase
    )

    @Provides
    fun provideSaveOneContactWidgetUseCase(
        oneContactWidgetRepository: OneContactWidgetRepository,
        addPictureUseCase: AddPictureUseCase<Bitmap>
    ) = SaveOneContactWidgetUseCase(oneContactWidgetRepository, addPictureUseCase)
}

