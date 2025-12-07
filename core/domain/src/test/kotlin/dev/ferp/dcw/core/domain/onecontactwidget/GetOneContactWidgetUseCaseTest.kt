package dev.ferp.dcw.core.domain.onecontactwidget

import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidgetRepository
import dev.ferp.dcw.core.domain.onecontactwidget.mother.OneContactWidgetMother
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetOneContactWidgetUseCaseTest {

    private val repository: OneContactWidgetRepository = mockk {
        coEvery {
            getWidget(any())
        } returns Result.success(OneContactWidgetMother.widget)
    }

    private val useCase = GetOneContactWidgetUseCase(repository)

    @Test
    fun `Use case returns repository result`() = runTest {
        val result = useCase(OneContactWidgetMother.widget.appWidgetId)
        assertTrue(result.isSuccess)
        assertEquals(OneContactWidgetMother.widget, result.getOrNull())
    }
}