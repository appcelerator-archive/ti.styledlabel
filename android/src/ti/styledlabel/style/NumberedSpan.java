/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.style;

import ti.styledlabel.Constants;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

public class NumberedSpan implements LeadingMarginSpan, ParcelableSpan {
	private final int mGapWidth;
	private final boolean mWantColor;
	private final int mColor;
	private final int mNumber;

	private static final int BULLET_RADIUS = 3;
	public static final int STANDARD_GAP_WIDTH = 2;

	public NumberedSpan(int number) {
		mGapWidth = STANDARD_GAP_WIDTH;
		mWantColor = false;
		mColor = 0;
		mNumber = number;
	}

	public NumberedSpan(int number, int gapWidth) {
		mGapWidth = gapWidth;
		mWantColor = false;
		mColor = 0;
		mNumber = number;
	}

	public NumberedSpan(int number, int gapWidth, int color) {
		mGapWidth = gapWidth;
		mWantColor = true;
		mColor = color;
		mNumber = number;
	}

	public NumberedSpan(int number, Parcel src) {
		mGapWidth = src.readInt();
		mWantColor = src.readInt() != 0;
		mColor = src.readInt();
		mNumber = number;
	}

	public int getSpanTypeId() {
		return Constants.NUMBERED_SPAN;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mGapWidth);
		dest.writeInt(mWantColor ? 1 : 0);
		dest.writeInt(mColor);
	}

	public int getLeadingMargin(boolean first) {
		return 2 * BULLET_RADIUS + mGapWidth;
	}

	public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top,
			int baseline, int bottom, CharSequence text, int start, int end,
			boolean first, Layout l) {
		if (((Spanned) text).getSpanStart(this) == start) {
			Paint.Style style = p.getStyle();
			int oldcolor = 0;

			if (mWantColor) {
				oldcolor = p.getColor();
				p.setColor(mColor);
			}

			p.setStyle(Paint.Style.FILL);

			c.drawText(mNumber + ".", x - ((mNumber + "").length() * 10), top + 19, p);

			if (mWantColor) {
				p.setColor(oldcolor);
			}

			p.setStyle(style);
		}
	}
}