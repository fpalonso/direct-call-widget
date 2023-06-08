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

package com.blaxsoftware.directcallwidget.ui

import android.content.res.ColorStateList
import android.net.Uri
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.data.model.AppInfo
import com.blaxsoftware.directcallwidget.data.model.Phone
import com.bumptech.glide.Glide

@BindingAdapter("app:uri")
fun ImageView.setUri(uri: Uri?) {
    if (uri != null) {
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .into(this)
        ContextCompat.getColor(context, R.color.black_54pct).also {
            ImageViewCompat.setImageTintList(this, null)
        }
    } else {
        setImageResource(R.drawable.ic_person)
        ContextCompat.getColor(context, R.color.black_54pct).also { tint ->
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(tint))
        }
    }
}

@BindingAdapter("app:phoneList")
fun AutoCompleteTextView.setPhoneList(phones: List<Phone>?) {
    val phoneArray = phones?.map { phone -> phone.number }?.toTypedArray() ?: emptyArray()
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, phoneArray).also {
        setAdapter(it)
    }
}


@BindingAdapter("app:appList")
fun AutoCompleteTextView.setAppList(appList: List<AppInfo>?) {
    val appArray = appList?.map { app -> app.appName }?.toTypedArray() ?: emptyArray()
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, appArray).also {
        setAdapter(it)
    }
}