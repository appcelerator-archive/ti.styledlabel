/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.parsing;

import org.appcelerator.titanium.proxy.TiViewProxy;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.HashMap;

public class CustomLinkMovementMethod extends LinkMovementMethod {

	private final TiViewProxy _proxy;

	public CustomLinkMovementMethod(TiViewProxy proxy) {
		_proxy = proxy;
	}

	@Override
	public boolean onTouchEvent(TextView widget, Spannable buffer,
			MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_UP) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			x -= widget.getTotalPaddingLeft();
			y -= widget.getTotalPaddingTop();

			x += widget.getScrollX();
			y += widget.getScrollY();

			Layout layout = widget.getLayout();
			int line = layout.getLineForVertical(y);
			int off = layout.getOffsetForHorizontal(line, x);

			ClickableSpan[] links = buffer.getSpans(off, off,
					ClickableSpan.class);

			if (links.length != 0 && links[0] instanceof URLSpan) {
				HashMap args = new HashMap();
				args.put("url", ((URLSpan) links[0]).getURL());
				_proxy.fireEvent("click", args);
				return true;
			}
		}
		return super.onTouchEvent(widget, buffer, event);
	}
}
