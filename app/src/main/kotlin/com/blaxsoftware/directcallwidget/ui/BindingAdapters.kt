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