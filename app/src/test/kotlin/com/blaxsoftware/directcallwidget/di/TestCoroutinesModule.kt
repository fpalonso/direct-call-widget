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

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.ferp.dcw.core.di.ApplicationScope
import dev.ferp.dcw.core.di.CoroutinesModule
import dev.ferp.dcw.core.di.IoDispatcher
import dev.ferp.dcw.core.di.MainDispatcher
import dev.ferp.dcw.core.util.test.TestDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutinesModule::class]
)
object TestCoroutinesModule {

    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = TestDispatcherProvider.testDispatcher

    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = TestDispatcherProvider.testDispatcher

    @ApplicationScope
    @Singleton
    @Provides
    fun provideAppScope(
        @MainDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}

