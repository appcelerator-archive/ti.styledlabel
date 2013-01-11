/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.property;

import java.util.HashMap;

import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;

public class TextAlign implements IProperty {

	@Override
	public void mixWithMap(HashMap<String, IProperty> map) {
		map.put("text-align", this);
	}

	@Override
	public ParcelableSpan getSpan(SpannableStringBuilder mSB, String value) {
		mSB.append('\n');
		if (value.equals("center")) {
			return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
		} else if (value.equals("right")) {
			return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE);
		} else {
			return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
		}
	}

}
