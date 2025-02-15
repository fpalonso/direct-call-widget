/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
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

package com.blaxsoftware.directcallwidget.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blaxsoftware.directcallwidget.domain.SaveOneContactWidgetUseCase
import com.blaxsoftware.directcallwidget.legacy.LegacyWidgets
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ferp.dcw.data.contacts.ContactRepository
import dev.ferp.dcw.data.phones.Phone
import dev.ferp.dcw.data.phones.PhoneRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    private val contactRepo: ContactRepository<Uri>,
    private val phoneRepo: PhoneRepository,
    private val saveWidgetUseCase: SaveOneContactWidgetUseCase,
    private val legacyWidgets: LegacyWidgets
) : ViewModel(), Observable {

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    var widgetId: Int? = null

    private val _picUri = MutableLiveData<Uri?>()
    val picUri: LiveData<Uri?>
        get() = _picUri

    @Bindable
    val displayName = MutableLiveData<String?>()

    private val _phoneList = MutableLiveData<List<Phone>?>()
    val phoneList: LiveData<List<Phone>?>
        get() = _phoneList

    @Bindable
    val phoneNumber = MutableLiveData<String?>()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    fun loadContact(contactUri: Uri) {
        viewModelScope.launch {
            contactRepo.getContactById(contactUri)?.let { contact ->
                _picUri.value = contact.photoUri?.toUri()
                displayName.value = contact.displayName
                contact.lookUpKey?.let { lookUpKey ->
                    _phoneList.value = phoneRepo.getPhoneListByLookUpKey(lookUpKey)
                }
                if (!_phoneList.value.isNullOrEmpty()) {
                    phoneNumber.value = _phoneList.value!![0].number
                }
            }
        }
    }

    fun onPictureSelected(pictureUri: Uri?) {
        _picUri.value = pictureUri
    }

    fun onAccept() {
        widgetId?.let { appWidgetId ->
            viewModelScope.launch {
                saveWidgetUseCase(
                    appWidgetId, displayName.value, phoneNumber.value ?: "", 0, _picUri.value
                )
            }
            legacyWidgets.update(intArrayOf(appWidgetId))
        }
    }
}