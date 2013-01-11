/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import org.ccil.cowan.tagsoup.Parser;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.kroll.common.AsyncResult;

import ti.styledlabel.parsing.CustomImageGetter;
import ti.styledlabel.parsing.CustomLinkMovementMethod;
import ti.styledlabel.parsing.HtmlParser;
import ti.styledlabel.parsing.HtmlToSpannedConverter;

import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.text.Spanned;

public class Label extends TiUIView {

	private String _html;
	private String[] _filteredTags;
	private int _filterTagsMode = -1;

	public Label(TiViewProxy proxy) {
		super(proxy);
		TextView textView = new TextView(TiApplication.getAppCurrentActivity());
		textView.setMovementMethod(new CustomLinkMovementMethod(proxy));
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setPadding(0, 0, 0, 0);
		textView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		textView.setKeyListener(null);
		textView.setTextColor(Color.BLACK);
		textView.setFocusable(false);
		setNativeView(textView);
	}

	@Override
	public void processProperties(KrollDict props) {
		super.processProperties(props);
		if (props.containsKey("html")) {
			setHtml(props.getString("html"));
		}
		if (props.containsKey("filteredTags")) {
			setFilteredTags(props.getStringArray("filteredTags"));
		}
		if (props.containsKey("filteredTagsMode")) {
			setFilteredTagsMode(props.getInt("filteredTagsMode"));
		}
	}

    private static final int MSG_UPDATE_TEXT = 50000;

	private final Handler handler = new Handler(TiMessenger.getMainMessenger().getLooper(), new Handler.Callback ()
	{
    	public boolean handleMessage(Message msg)
        {
            switch (msg.what) {
                case MSG_UPDATE_TEXT: {
                    AsyncResult result = (AsyncResult) msg.obj;
                    handleUpdateText((Spanned) result.getArg());
                    result.setResult(null);
                    return true;
                }
            }
            return false;
        }
	});

	private void updateText(final Spanned text)
	{
	    if (!TiApplication.isUIThread()) {
	        TiMessenger.sendBlockingMainMessage(handler.obtainMessage(MSG_UPDATE_TEXT), text);
	    } else {
	        handleUpdateText(text);
	    }
	}

	private void handleUpdateText(final Spanned text)
	{
	    TextView textView = (TextView) getNativeView();
	    textView.setText(text);
	    textView.invalidate();
	}

	public void setHtml(String html) {
		_html = html;
		Parser parser = new Parser();
		try {
			parser.setProperty(Parser.schemaProperty, HtmlParser.schema);
		} catch (org.xml.sax.SAXNotRecognizedException e) {
			// Should not happen.
			throw new RuntimeException(e);
		} catch (org.xml.sax.SAXNotSupportedException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}

		HtmlToSpannedConverter converter = new HtmlToSpannedConverter(html, new CustomImageGetter(proxy), parser);
		converter.setFilteredTags(_filteredTags);
		converter.setFilteredTagsMode(_filterTagsMode);

        updateText(converter.convert());
	}

	public void setFilteredTags(String[] tags) {
		_filteredTags = tags;
		if (_filteredTags != null && _filterTagsMode != -1) {
			setHtml(_html);
		}
	}

	public void setFilteredTagsMode(int mode) {
		_filterTagsMode = mode;
		if (_filteredTags != null && _filterTagsMode != -1) {
			setHtml(_html);
		}
	}
}
