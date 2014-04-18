/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.property;

import java.util.HashMap;

import ti.styledlabel.Util;

import android.text.ParcelableSpan;
import android.text.SpannableStringBuilder;

public class FontSize implements IProperty {

	@Override
	public void mixWithMap(HashMap<String, IProperty> map) {
		map.put("font-size", this);
	}

	@Override
	public ParcelableSpan getSpan(SpannableStringBuilder mSB, String value) {
		return Util.sizeToSpan(value);
	}

}
