/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;

import java.util.HashMap;

@Kroll.proxy(creatableInModule = StyledlabelModule.class)
public class LabelProxy extends TiViewProxy {

	public LabelProxy() {
		super();
	}

	@Override
	public TiUIView createView(Activity activity) {
		return new Label(this);
	}

	@Override
	public boolean fireEvent(String event, Object args) {
		// Suppress click events that didn't come from us (no "url" property).
    	if (event.equals("click") &&
    	    (args instanceof HashMap) &&
    	    !((HashMap)args).containsKey("url")) {
			return false;
		}
		return super.fireEvent(event, args);
	}

    @Kroll.setProperty
	public void setHtml(String html)
	{
		((Label) getOrCreateView()).setHtml(html);
	}

    @Kroll.setProperty
	public void setFilteredTags(String[] tags)
	{
		((Label) getOrCreateView()).setFilteredTags(tags);
	}

    @Kroll.setProperty
	public void setFilteredTagsMode(int mode)
	{
		((Label) getOrCreateView()).setFilteredTagsMode(mode);
	}
}
