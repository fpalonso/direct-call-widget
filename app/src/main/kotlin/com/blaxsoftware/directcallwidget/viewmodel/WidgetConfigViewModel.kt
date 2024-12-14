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

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.UiThread
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blaxsoftware.directcallwidget.data.model.AppInfo
import com.blaxsoftware.directcallwidget.data.model.Phone
import com.blaxsoftware.directcallwidget.data.model.WidgetData
import com.blaxsoftware.directcallwidget.data.source.ContactDataSource
import com.blaxsoftware.directcallwidget.data.source.WidgetDataSource
import com.blaxsoftware.directcallwidget.data.source.WidgetPicDataSource
import kotlinx.coroutines.launch


@UiThread
class WidgetConfigViewModel(
    private val contactDataSource: ContactDataSource,
    private val widgetDataSource: WidgetDataSource,
    private val widgetPicDataSource: WidgetPicDataSource,
    context: android.content.Context
) : ViewModel(), Observable {

    private val context by lazy { context }
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    var widgetId: Int? = null

    private var contactId: String? = null

    private val _picUri = MutableLiveData<Uri?>()
    val picUri: LiveData<Uri?>
        get() = _picUri

    @Bindable
    val displayName = MutableLiveData<String?>()

    private val _appList = MutableLiveData<List<AppInfo>?>(
        listOf(

                    AppInfo(
                        null,
                        "Default Call App"

                )
        )
    )
    val appList: LiveData<List<AppInfo>?>
        get() = _appList

    private val _phoneList = MutableLiveData<List<Phone>?>()
    val phoneList: LiveData<List<Phone>?>
        get() = _phoneList

    @Bindable
    val phoneNumber = MutableLiveData<String?>()

    @Bindable
    val selectedApp = MutableLiveData<String?>()

    private val _result = MutableLiveData<ConfigResult>()
    val result: LiveData<ConfigResult> = _result

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    fun loadContact(contactUri: Uri) {
        viewModelScope.launch {
            selectedApp.value = "Default Call App"
            contactDataSource.getContactByUri(contactUri)?.let { contact ->
                _picUri.value = contact.photoUri
                displayName.value = contact.displayName
                _phoneList.value = contact.phoneList
                contactId = contact.contactId
                if (contact.phoneList.isNotEmpty()) {
                    phoneNumber.value = contact.phoneList[0].number
                }
                if(isAppInstalled("com.whatsapp")){
                    _appList.value = listOf(
                        AppInfo(
                            null,
                            "Default Call App"
                        ),
                        AppInfo(
                            "com.whatsapp",
                            "WhatsApp"
                        )
                    )
                }
            }
        }
    }

    fun onPictureSelected(pictureUri: Uri?) {
        _picUri.value = pictureUri
    }

    fun onAccept() {
        viewModelScope.launch {
            copyPictureToInternalFolder()
            saveWidgetData()
        }
    }

    private suspend fun copyPictureToInternalFolder() {
        _picUri.value?.let { picUri ->
            try {
                val internalFile = widgetPicDataSource.insertFromUri(picUri)
                _picUri.value = Uri.fromFile(internalFile)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun saveWidgetData() {
        widgetId?.let { widgetId ->
            val widgetData = WidgetData(
                    widgetId,
                    displayName.value,
                    phoneNumber.value ?: "",
                    0, // FIXME this property is no longer valid
                    picUri.value?.toString(),
                    getSelectedPackageName(),
                    contactId
//                selectedApp.value?.packageName
            )
            with(widgetDataSource) {
                insertWidgetData(widgetData)
                _result.value = ConfigResult(accepted = true, widgetData = widgetData)
            }
        }
    }

    fun onCancel() {
        _result.value = ConfigResult(accepted = false)
    }

    private fun getSelectedPackageName() : String? {
        var selectedPackageName: String? = null
        for(app in appList.value!!)
        {
            if(app.appName == selectedApp.value){
                selectedPackageName = app.packageName
                break
            }
        }

        return selectedPackageName
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               context.packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                context.packageManager
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            }
            Log.d("whatsapp installed", "WhatsApp Installed")
            true
        } catch (ignored: PackageManager.NameNotFoundException) {
            Log.d("whatsapp installed", "WhatsApp Not Installed Or Exception")
            false
        }
    }
}

data class ConfigResult(
        val accepted: Boolean,
        val widgetData: WidgetData? = null
)


















