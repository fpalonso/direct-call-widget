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
