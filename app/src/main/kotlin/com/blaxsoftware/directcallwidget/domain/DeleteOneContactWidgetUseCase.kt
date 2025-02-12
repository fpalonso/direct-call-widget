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

package com.blaxsoftware.directcallwidget.domain

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import dev.ferp.dcw.core.di.IoDispatcher
import dev.ferp.dcw.data.contacts.OneContactWidgetRepository
import dev.ferp.dcw.data.pictures.WidgetPictureRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteOneContactWidgetUseCase @Inject constructor(
    private val widgetRepository: OneContactWidgetRepository,
    private val pictureRepository: WidgetPictureRepository<Uri, Uri, Bitmap, Int>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(appWidgetId: Int) = withContext(ioDispatcher) {
        val pictureUri = widgetRepository.getWidget(appWidgetId)?.pictureUri
        pictureUri?.toUri()?.let { picUri ->
            pictureRepository.deletePicture(picUri)
        }
        widgetRepository.deleteWidget(appWidgetId)
    }
}