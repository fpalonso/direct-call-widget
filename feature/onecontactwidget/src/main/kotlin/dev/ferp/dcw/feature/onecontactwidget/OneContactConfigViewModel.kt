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

package dev.ferp.dcw.feature.onecontactwidget

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ferp.dcw.core.domain.data.PhoneType
import dev.ferp.dcw.core.domain.data.onecontactwidget.OneContactWidget
import dev.ferp.dcw.core.domain.onecontactwidget.GetOneContactWidgetUseCase
import dev.ferp.dcw.core.domain.onecontactwidget.SaveOneContactWidgetUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WidgetState {
    data object Loading : WidgetState
    data class LoadFinished(val widget: OneContactWidget) : WidgetState
    data object LoadFailed : WidgetState
}

data class OneContactConfigUiState(
    val widgetState: WidgetState = WidgetState.Loading
)

@HiltViewModel
class OneContactConfigViewModel @Inject constructor(
    private val getOneContactWidgetUseCase: GetOneContactWidgetUseCase,
    private val saveOneContactWidgetUseCase: SaveOneContactWidgetUseCase<Bitmap>
) : ViewModel() {

    var uiState by mutableStateOf(OneContactConfigUiState())
        private set

    fun loadWidget(appWidgetId: Int) {
        viewModelScope.launch {
            getOneContactWidgetUseCase(appWidgetId)
                .onSuccess { widget ->
                    uiState = uiState.copy(
                        widgetState = WidgetState.LoadFinished(widget)
                    )
                }
                .onFailure {
                    uiState = uiState.copy(
                        widgetState = WidgetState.LoadFailed
                    )
                }
        }
    }

    fun saveWidget(
        appWidgetId: Int,
        phoneNumber: String,
        displayName: String? = null,
        phoneType: PhoneType = PhoneType.UNKNOWN,
        pictureUri: String? = null
    ) {
        viewModelScope.launch {
            saveOneContactWidgetUseCase(
                appWidgetId = appWidgetId,
                displayName = displayName,
                phoneNumber = phoneNumber,
                phoneType = phoneType,
                pictureUri = pictureUri
            )
        }
    }
}