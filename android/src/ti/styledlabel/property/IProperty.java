/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.property;

import java.util.HashMap;

import android.text.ParcelableSpan;
import android.text.SpannableStringBuilder;

public interface IProperty {

	void mixWithMap(HashMap<String, IProperty> map);

	ParcelableSpan getSpan(SpannableStringBuilder mSB, String value);

}
