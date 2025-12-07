package dev.ferp.dcw.core.domain.onecontactwidget

import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidget
import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidgetRepository

class GetOneContactWidgetUseCase(
    private val repository: OneContactWidgetRepository
) {
    suspend operator fun invoke(appWidgetId: Int): Result<OneContactWidget> {
        return repository.getWidget(appWidgetId)
    }
}