/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.property;

import java.util.HashMap;

import android.graphics.Typeface;
import android.text.ParcelableSpan;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

public class FontWeight implements IProperty {

	@Override
	public void mixWithMap(HashMap<String, IProperty> map) {
		map.put("font-weight", this);
	}

	@Override
	public ParcelableSpan getSpan(SpannableStringBuilder mSB, String value) {
		return new StyleSpan(value.equals("normal") || value.equals("400") ? Typeface.NORMAL : Typeface.BOLD);
	}

}
