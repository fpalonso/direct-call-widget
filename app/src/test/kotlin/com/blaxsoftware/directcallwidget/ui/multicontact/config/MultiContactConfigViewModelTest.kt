package com.blaxsoftware.directcallwidget.ui.multicontact.config

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.blaxsoftware.directcallwidget.data.ContactConfig
import com.blaxsoftware.directcallwidget.rules.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dev.ferp.dcw.data.pictures.WidgetPictureRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MultiContactConfigViewModelTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var pictureRepo: WidgetPictureRepository<Uri, Uri, Bitmap, Int>

    private lateinit var context: Context
    private lateinit var viewModel: MultiContactConfigViewModel

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext<HiltTestApplication>()
        viewModel = MultiContactConfigViewModel(pictureRepo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addsContactWithInternalPictureUri() = runTest {
        // Given
        val originalPicUri = File.createTempFile("tmp", null, context.filesDir).toUri()

        // When
        viewModel.addContact(
            ContactConfig(pictureUri = originalPicUri.toString())
        )
        advanceUntilIdle()

        // Then
        val finalPictureUri = viewModel.uiState.contacts[0].pictureUri.toUri()
        assertThat(finalPictureUri).isNotEqualTo(originalPicUri)
        val expectedPathPrefix = File(context.filesDir, "pics")
        assertThat(finalPictureUri.path).startsWith(expectedPathPrefix.path)
    }
}