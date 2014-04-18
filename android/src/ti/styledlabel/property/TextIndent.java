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
import android.text.style.LeadingMarginSpan;

public class TextIndent implements IProperty {

	@Override
	public void mixWithMap(HashMap<String, IProperty> map) {
		map.put("text-indent", this);
	}

	private float sizeToUnit(String value) {
		if (value == null || value.length() == 0)
			return 14;
		int unitLength = 2;
		if (value.endsWith("%"))
			unitLength = 1;
		String units = value.substring(value.length() - unitLength);
		float size = Float.parseFloat(value.substring(0, value.length() - unitLength));
		if (units.equals("px") || units.equals("dp")) {
			return size;
		} else if (units.equals("%") || units.equals("em")) {
			return size / 100f;
		} else {
			Util.e("Unsupported measurement used, please use px, %, or dp: " + value);
			return 14;
		}
	}

	@Override
	public ParcelableSpan getSpan(SpannableStringBuilder mSB, String value) {
		mSB.append('\n');
		return new LeadingMarginSpan.Standard((int) sizeToUnit(value), 0);
	}

}
