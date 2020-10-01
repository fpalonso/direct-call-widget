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

package com.blaxsoftware.directcallwidget.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.blaxsoftware.directcallwidget.R
import kotlinx.android.synthetic.main.menu_item_with_icon.view.*

class MenuItemWithIcon(
        context: Context,
        attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    init {
        View.inflate(context, R.layout.menu_item_with_icon, this)
        context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.MenuItemWithIcon,
                0,
                0
        ).apply {
            getResourceId(R.styleable.MenuItemWithIcon_icon, 0).also {
                imageView.setImageResource(it)
            }
            textView.text = getString(R.styleable.MenuItemWithIcon_android_text)
            recycle()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        root.setOnClickListener(l)
    }
}
