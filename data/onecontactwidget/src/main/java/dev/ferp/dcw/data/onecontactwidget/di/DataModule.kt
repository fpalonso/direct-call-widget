package dev.ferp.dcw.data.onecontactwidget.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidgetRepository
import dev.ferp.dcw.data.onecontactwidget.DefaultOneContactWidgetRepository
import dev.ferp.dcw.data.onecontactwidget.source.OneContactWidgetDataSource
import dev.ferp.dcw.data.onecontactwidget.source.local.OneContactWidgetPreferencesDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindOneContactWidgetDataSource(
        impl: OneContactWidgetPreferencesDataSource
    ): OneContactWidgetDataSource

    @Binds
    abstract fun bindOneContactWidgetRepository(
        impl: DefaultOneContactWidgetRepository
    ): OneContactWidgetRepository
}