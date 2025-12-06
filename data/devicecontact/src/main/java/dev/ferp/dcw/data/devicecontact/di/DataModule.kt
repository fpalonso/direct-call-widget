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

package dev.ferp.dcw.data.devicecontact.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ferp.dcw.core.domain.data.devicecontact.DeviceContactRepository
import dev.ferp.dcw.data.devicecontact.DefaultDeviceContactRepository
import dev.ferp.dcw.data.devicecontact.source.DeviceContactDataSource
import dev.ferp.dcw.data.devicecontact.source.local.DeviceContactProviderDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindDeviceContactDataSource(
        impl: DeviceContactProviderDataSource
    ): DeviceContactDataSource

    @Binds
    abstract fun bindDeviceContactRepository(
        impl: DefaultDeviceContactRepository
    ): DeviceContactRepository
}