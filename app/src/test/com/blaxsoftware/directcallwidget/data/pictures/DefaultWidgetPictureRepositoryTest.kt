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

package com.blaxsoftware.directcallwidget.data.pictures

import android.content.Context
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.blaxsoftware.directcallwidget.DirectCallWidgetApp
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class DefaultWidgetPictureRepositoryTest {

    private lateinit var context: Context
    private lateinit var pictureRepo: WidgetPictureRepository

    @Before
    fun initPictureRepo() {
        context = ApplicationProvider.getApplicationContext<DirectCallWidgetApp>()
        pictureRepo = DefaultWidgetPictureRepository(
            contentResolver = context.contentResolver,
            picturesDir = context.filesDir
        )
    }

    @Test
    fun pictureIsCopiedToInternalStorage() = runTest {
        // Given
        val tempFile = File.createTempFile("test", null, context.filesDir)

        // When
        val internalFileUri = pictureRepo.copyFromUri(tempFile.toUri())

        // Then
        assertThat(internalFileUri.toFile().exists()).isTrue()
    }

    @Test
    fun pictureIsDeleted() = runTest {
        // Given
        val tempFile = File.createTempFile("test", null, context.filesDir)
        assertThat(tempFile.exists()).isTrue()

        // When
        pictureRepo.delete(tempFile.toUri())

        // Then
        assertThat(tempFile.exists()).isFalse()
    }
}