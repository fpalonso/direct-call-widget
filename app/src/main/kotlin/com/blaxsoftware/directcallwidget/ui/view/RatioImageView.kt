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
