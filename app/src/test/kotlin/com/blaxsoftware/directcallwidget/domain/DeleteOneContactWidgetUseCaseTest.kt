package com.blaxsoftware.directcallwidget.domain

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.data.contacts.OneContactWidgetRepository
import dev.ferp.dcw.data.onecontactwidget.FakeOneContactWidgetRepository
import dev.ferp.dcw.data.pictures.FakeWidgetPictureRepository
import dev.ferp.dcw.data.pictures.WidgetPictureRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteOneContactWidgetUseCaseTest {

    private val externalPictureUri = "content://sd/pictures/1".toUri()

    private lateinit var widgetRepo: OneContactWidgetRepository
    private lateinit var pictureRepo: WidgetPictureRepository<Uri, Uri, Bitmap, Int>

    @Before
    fun setUp() {
        widgetRepo = FakeOneContactWidgetRepository()
        pictureRepo = FakeWidgetPictureRepository()
    }

    @Test
    fun `deleteOneContactWidgetUseCase deletes a widget data`() = runTest {
        // Given
        val internalPictureUri = pictureRepo.addPicture(externalPictureUri)
        assertThat(pictureRepo.getPicture(internalPictureUri.getOrThrow(), 1, 1)).isNotNull()
        widgetRepo.createWidget(
            appWidgetId = 1,
            displayName = "John Doe",
            phoneNumber = "1234567890",
            phoneType = 1,
            pictureUri = internalPictureUri.getOrNull()?.toString() ?: ""
        )
        assertThat(widgetRepo.getWidget(1)).isNotNull()
        val deleteWidgetUseCase = DeleteOneContactWidgetUseCase(
            widgetRepository = widgetRepo,
            pictureRepository = pictureRepo
        )

        // When
        deleteWidgetUseCase(appWidgetId = 1)

        // Then
        assertThat(pictureRepo.getPicture(internalPictureUri.getOrThrow(), 1, 1).isFailure).isTrue()
        assertThat(widgetRepo.getWidget(1)).isNull()
    }
}