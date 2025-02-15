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
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.databinding.MenuItemWithIconBinding

class MenuItemWithIcon(
        context: Context,
        attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    private val viewBinding: MenuItemWithIconBinding

    init {
        viewBinding = MenuItemWithIconBinding.inflate(LayoutInflater.from(context), this, true)
        context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.MenuItemWithIcon,
                0,
                0
        ).apply {
            getResourceId(R.styleable.MenuItemWithIcon_icon, 0).also {
                viewBinding.imageView.setImageResource(it)
            }
            viewBinding.textView.text = getString(R.styleable.MenuItemWithIcon_android_text)
            recycle()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        viewBinding.root.setOnClickListener(l)
    }
}
