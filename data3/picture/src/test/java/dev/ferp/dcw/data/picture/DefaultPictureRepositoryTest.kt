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

package dev.ferp.dcw.data.picture

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import dev.ferp.dcw.core.domain.data.picture.PictureRepository
import dev.ferp.dcw.data.picture.doubles.PictureMemoryDataSource
import dev.ferp.dcw.data.picture.source.PictureDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultPictureRepositoryTest {

    private lateinit var dataSource: PictureDataSource
    private lateinit var repository: PictureRepository<Bitmap>

    @Before
    fun init() {
        dataSource = PictureMemoryDataSource()
        repository = DefaultPictureRepository(dataSource)
    }

    @Test
    fun `Add and retrieve picture`() = runTest {
        val addPictureResult = repository.addPicture("uri")
        Truth.assertThat(addPictureResult.isSuccess).isTrue()
        val pictureUri = addPictureResult.getOrNull()
        val getPictureResult = repository.getPicture(pictureUri.orEmpty(), 100, 100)
        Truth.assertThat(getPictureResult.isSuccess).isTrue()
        Truth.assertThat(getPictureResult.getOrNull()).isNotNull()
    }

    @Test
    fun `getPicture returns failure for non-existing URI`() = runTest {
        val getPictureResult = repository.getPicture("fakeUri", 100, 100)
        Truth.assertThat(getPictureResult.isFailure).isTrue()
    }

    @Test
    fun `Add and delete picture`() = runTest {
        val addPictureResult = repository.addPicture("uri")
        val pictureUri = addPictureResult.getOrNull()
        repository.deletePicture(pictureUri.orEmpty())
        val getPictureResult = repository.getPicture(pictureUri.orEmpty(), 100, 100)
        Truth.assertThat(getPictureResult.isFailure).isTrue()
    }
}