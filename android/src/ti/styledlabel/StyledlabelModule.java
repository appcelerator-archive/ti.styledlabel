/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

@Kroll.module(name="Styledlabel", id="ti.styledlabel")
public class StyledlabelModule extends KrollModule
{
	public StyledlabelModule() {
		super();
	}
	
	@Kroll.constant
	public static final int INCLUDE_SPECIFIED_TAGS_ONLY = 0;
	@Kroll.constant
	public static final int EXCLUDE_SPECIFIED_TAGS = 1;
}
