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

package dev.ferp.dcw.data.onecontactwidget

import dev.ferp.dcw.core.domain.data.PhoneType
import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidget
import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidgetRepository
import dev.ferp.dcw.data.onecontactwidget.source.local.OneContactWidgetDataSource
import javax.inject.Inject

internal class DefaultOneContactWidgetRepository @Inject constructor(
    private val dataSource: OneContactWidgetDataSource
) : OneContactWidgetRepository {

    override suspend fun addWidget(
        appWidgetId: Int,
        phoneNumber: String,
        displayName: String?,
        phoneType: PhoneType,
        pictureUri: String?
    ) {
        dataSource.addWidget(
            appWidgetId = appWidgetId,
            displayName = displayName,
            phoneNumber = phoneNumber,
            phoneType = phoneType.toLocal(),
            pictureUri = pictureUri
        )
    }

    override suspend fun getWidget(appWidgetId: Int): Result<OneContactWidget> {
        return dataSource.getWidget(appWidgetId).fold(
            onSuccess = { widget ->
                Result.success(
                    OneContactWidget(
                        appWidgetId = widget.appWidgetId,
                        displayName = widget.displayName,
                        phoneNumber = widget.phoneNumber,
                        phoneType = localPhoneTypeToDomain(widget.phoneType),
                        pictureUri = widget.pictureUri
                    )
                )
            },
            onFailure = { throwable ->
                Result.failure(throwable)
            }
        )
    }

    override suspend fun deleteWidget(appWidgetId: Int): Boolean {
        return dataSource.deleteWidget(appWidgetId)
    }

    override suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            dataSource.deleteWidget(id)
        }
    }
}