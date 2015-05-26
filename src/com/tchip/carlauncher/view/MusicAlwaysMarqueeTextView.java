package com.tchip.carlauncher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MusicAlwaysMarqueeTextView extends TextView {

	public MusicAlwaysMarqueeTextView(Context context) {
		super(context);
	}

	public MusicAlwaysMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MusicAlwaysMarqueeTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

}
