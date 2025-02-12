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
class SaveOneContactWidgetUseCaseTest {

    private val externalPictureUri = "content://sd/pictures/1".toUri()

    private lateinit var widgetRepo: OneContactWidgetRepository
    private lateinit var pictureRepo: WidgetPictureRepository<Uri, Uri, Bitmap, Int>

    @Before
    fun setUp() {
        widgetRepo = FakeOneContactWidgetRepository()
        pictureRepo = FakeWidgetPictureRepository()
    }


    @Test
    fun `saveOneContactWidgetUseCase saves a widget data`() = runTest {
        // Given
        val saveWidgetUseCase = SaveOneContactWidgetUseCase(
            widgetRepository = widgetRepo,
            pictureRepository = pictureRepo
        )

        // When
        saveWidgetUseCase(
            appWidgetId = 1,
            displayName = "John Doe",
            phoneNumber = "1234567890",
            phoneType = 1,
            selectedPictureUri = externalPictureUri
        )

        // Then
        val widget = widgetRepo.getWidget(appWidgetId = 1)
        assertThat(widget).isNotNull()
        assertThat(widget?.displayName).isEqualTo("John Doe")
        assertThat(widget?.phoneNumber).isEqualTo("1234567890")
        assertThat(widget?.phoneType).isEqualTo(1)
        val pictureUri = widget?.pictureUri
        assertThat(pictureUri).isNotEqualTo(externalPictureUri.toString())
        val picture = pictureRepo.getPicture(pictureUri?.toUri() ?: Uri.EMPTY, 1, 1)
        assertThat(picture).isNotNull()
    }
}