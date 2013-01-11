/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.property;

import java.util.HashMap;

public class PropertyManager {

	private static PropertyManager _instance;

	public static PropertyManager getInstance() {
		if (_instance == null) {
			_instance = new PropertyManager();
		}
		return _instance;
	}

	private HashMap<String, IProperty> _children;

	private PropertyManager() {
		_children = new HashMap<String, IProperty>();

		new BackgroundColor().mixWithMap(_children);
		new Color().mixWithMap(_children);
		new FontSize().mixWithMap(_children);
		new FontWeight().mixWithMap(_children);
		new TextAlign().mixWithMap(_children);
		new TextIndent().mixWithMap(_children);
	}

	public IProperty getProperty(String name) {
		return _children.get(name);
	}

}
