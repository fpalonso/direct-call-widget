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

package com.blaxsoftware.directcallwidget.rules

import androidx.test.core.app.ApplicationProvider
import com.blaxsoftware.directcallwidget.DirectCallWidgetApp
import com.blaxsoftware.directcallwidget.data.pictures.DefaultWidgetPictureRepository
import com.blaxsoftware.directcallwidget.data.pictures.WidgetPictureRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

class WidgetPictureRepositoryRule(
    private val ioDispatcher: CoroutineDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    lateinit var pictureRepository: WidgetPictureRepository
        private set

    override fun starting(description: Description?) {
        super.starting(description)
        val context = ApplicationProvider.getApplicationContext<DirectCallWidgetApp>()
        pictureRepository = DefaultWidgetPictureRepository(
            contentResolver = context.contentResolver,
            picturesDir = File(context.filesDir, "pics"),
            ioDispatcher = ioDispatcher
        )
    }
}