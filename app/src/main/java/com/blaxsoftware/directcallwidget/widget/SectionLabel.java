package com.blaxsoftware.directcallwidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.blaxsoftware.directcallwidget.R;

public class SectionLabel extends TextView {

    private boolean mExpanded;
    private ExpandedStateListener mExpandedStateListener;

    public interface ExpandedStateListener {

	void onExpandedStateChanged(boolean newState);
    }

    public SectionLabel(Context context) {
	this(context, null);
    }

    public SectionLabel(Context context, AttributeSet attrs) {
	this(context, attrs, R.attr.sectionLabelStyle);
    }

    public SectionLabel(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	inflate(context, R.layout.section_label, null);

	TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
		R.styleable.SectionLabel, defStyle, 0);

	String text = ta.getString(R.styleable.SectionLabel_android_text);
	setText(text);
	boolean expanded = ta.getBoolean(R.styleable.SectionLabel_expanded,
		false);
	setExpanded(expanded);
	ta.recycle();

	setOnTouchListener(new OnTouchListener() {

	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		    setExpanded(!mExpanded);
		    return true;
		}
		return false;
	    }
	});
    }

    public void setExpanded(boolean b) {
	mExpanded = b;
	int resId = b ? R.drawable.ic_navigation_expand_less_12dp
		: R.drawable.ic_navigation_expand_more_12dp;
	setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
	if (mExpandedStateListener != null) {
	    mExpandedStateListener.onExpandedStateChanged(b);
	}
    }

    public boolean isExpanded() {
	return mExpanded;
    }

    public ExpandedStateListener getExpandedStateListener() {
	return mExpandedStateListener;
    }

    public void setExpandedStateListener(
	    ExpandedStateListener expandedStateListener) {
	mExpandedStateListener = expandedStateListener;
    }
}
