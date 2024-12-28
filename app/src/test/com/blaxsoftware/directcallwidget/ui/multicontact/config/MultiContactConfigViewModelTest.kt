package com.blaxsoftware.directcallwidget.ui.multicontact.config

import android.content.Context
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.blaxsoftware.directcallwidget.DirectCallWidgetApp
import com.blaxsoftware.directcallwidget.data.ContactConfig
import com.blaxsoftware.directcallwidget.rules.MainDispatcherRule
import com.blaxsoftware.directcallwidget.rules.WidgetPictureRepositoryRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class MultiContactConfigViewModelTest {

    private lateinit var context: Context
    private lateinit var viewModel: MultiContactConfigViewModel

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @get:Rule
    val pictureRepositoryRule = WidgetPictureRepositoryRule(testDispatcher)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<DirectCallWidgetApp>()
        viewModel = MultiContactConfigViewModel(pictureRepositoryRule.pictureRepository)
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