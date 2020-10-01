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
import androidx.appcompat.widget.AppCompatImageView
import com.blaxsoftware.directcallwidget.R

class RatioImageView(
        context: Context,
        attributes: AttributeSet
) : AppCompatImageView(context, attributes) {

    private var widthFactor: Float? = null
    private var heightFactor: Float? = null

    init {
        context.theme.obtainStyledAttributes(
                attributes,
                R.styleable.RatioImageView,
                0,
                0
        ).apply {

            try {
                if (hasValue(R.styleable.RatioImageView_widthFactor)) {
                    widthFactor = getFloat(R.styleable.RatioImageView_widthFactor, 0f)
                }
                if (hasValue(R.styleable.RatioImageView_heightFactor)) {
                    heightFactor = getFloat(R.styleable.RatioImageView_heightFactor, 0f)
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        checkOnlyOneFactorIsNotNull()
        widthFactor?.let { factor ->
            val h = MeasureSpec.getSize(heightMeasureSpec)
            val w = h * factor
            val measuredWidth = View.resolveSizeAndState(w.toInt(), widthMeasureSpec, 1)
            setMeasuredDimension(measuredWidth, heightMeasureSpec)
            return
        }
        heightFactor?.let { factor ->
            val w = MeasureSpec.getSize(widthMeasureSpec)
            val h = w * factor
            val measuredHeight = View.resolveSizeAndState(h.toInt(), heightMeasureSpec, 1)
            setMeasuredDimension(widthMeasureSpec, measuredHeight)
            return
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    private fun checkOnlyOneFactorIsNotNull() {
        if (widthFactor != null && heightFactor != null) {
            throw IllegalStateException("Only widthFactor or heightFactor may be not null")
        }
    }
}
