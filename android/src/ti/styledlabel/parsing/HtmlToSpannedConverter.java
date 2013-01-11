/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.parsing;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import ti.styledlabel.StyledlabelModule;
import ti.styledlabel.Util;
import ti.styledlabel.property.IProperty;
import ti.styledlabel.property.PropertyManager;
import ti.styledlabel.style.NumberedSpan;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

/**
 * Note that I jacked this class from the Android source code so that I could expand it to offer up better support for HTML and CSS.
 */
public class HtmlToSpannedConverter implements ContentHandler {

	private static final float[] HEADER_SIZES = { 1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f, };

	private String mSource;
	private XMLReader mReader;
	private SpannableStringBuilder mSB;
	private Html.ImageGetter mImageGetter;

	private HashMap<String, Boolean> mFilteredTags = new HashMap<String, Boolean>();
	private int mFilterTagsMode = -1;

	private HashMap<String, String> mStyles = new HashMap<String, String>();
	private HashMap<String, Stack<TagMarker>> mStyleStack = new HashMap<String, Stack<TagMarker>>();

	private class TagMarker {
		private int mStart;
		private Attributes mAttributes;

		public TagMarker(int start, Attributes attributes) {
			mStart = start;
			mAttributes = attributes;
		}

		public int getStart() {
			return mStart;
		}

		public Attributes getAttributes() {
			return mAttributes;
		}
	}

	private void pushTag(String ltag, Attributes attributes) {
		if (!mStyleStack.containsKey(ltag)) {
			mStyleStack.put(ltag, new Stack<TagMarker>());
		}
		mStyleStack.get(ltag).push(new TagMarker(mSB.length(), attributes));
	}

	private TagMarker popTag(SpannableStringBuilder text, String ltag) {
		if (!mStyleStack.containsKey(ltag)) {
			Util.e("Found an unmatched closing tag " + ltag + "!");
			return null;
		}
		return mStyleStack.get(ltag).pop();
	}

	private static final HashMap<String, Boolean> SIMPLE_TAGS = buildSimpleTags();
	private static final HashMap<String, Boolean> IGNORED_TAGS = buildIgnoredTags();

	public HtmlToSpannedConverter(String source, Html.ImageGetter imageGetter, Parser parser) {
		mSource = source;
		mSB = new SpannableStringBuilder();
		mImageGetter = imageGetter;
		mReader = parser;
	}

	private static HashMap<String, Boolean> buildSimpleTags() {
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("span", true);
		map.put("em", true);
		map.put("b", true);
		map.put("strong", true);
		map.put("cite", true);
		map.put("dfn", true);
		map.put("i", true);
		map.put("big", true);
		map.put("small", true);
		map.put("tt", true);
		map.put("u", true);
		map.put("strike", true);
		map.put("s", true);
		map.put("del", true);
		map.put("pre", true);
		map.put("code", true);
		map.put("sup", true);
		map.put("sub", true);
		map.put("li", true);
		map.put("style", true);
		return map;
	}

	private static HashMap<String, Boolean> buildIgnoredTags() {
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("html", true);
		map.put("head", true);
		map.put("body", true);
		return map;
	}

	public void setFilteredTags(String[] tags) {
		mFilteredTags.clear();
		if (tags == null)
			return;
		for (String tag : tags) {
			mFilteredTags.put(tag, true);
		}
	}

	public void setFilteredTagsMode(int mode) {
		mFilterTagsMode = mode;
	}

	public Spanned convert() {
		mReader.setContentHandler(this);
		try {
			mReader.parse(new InputSource(new StringReader(mSource)));
		} catch (IOException e) {
			// We are reading from a string. There should not be IO problems.
			throw new RuntimeException(e);
		} catch (SAXException e) {
			// TagSoup doesn't throw parse exceptions.
			throw new RuntimeException(e);
		}

		// Fix flags and range for paragraph-type markup.
		Object[] obj = mSB.getSpans(0, mSB.length(), ParagraphStyle.class);
		for (int i = 0; i < obj.length; i++) {
			int start = mSB.getSpanStart(obj[i]);
			int end = mSB.getSpanEnd(obj[i]);

			// If the last line of the range is blank, back off by one.
			if (end - 2 >= 0) {
				if (mSB.charAt(end - 1) == '\n' && mSB.charAt(end - 2) == '\n') {
					end--;
				}
			}

			if (end == start) {
				mSB.removeSpan(obj[i]);
			} else {
				mSB.setSpan(obj[i], start, end, Spannable.SPAN_PARAGRAPH);
			}
		}

		return mSB;
	}

	private void handleStartTag(String tag, Attributes attributes) {

		String ltag = tag.toLowerCase();

		// keep track of this tag in our data structures; we'll retrieve it when
		// it is closed.
		pushTag(ltag, attributes);

		if (mFilterTagsMode == StyledlabelModule.EXCLUDE_SPECIFIED_TAGS) {
			if (mFilteredTags.containsKey(ltag)) {
				// Force it to be an innocuous span so that its contents are
				// still output.
				ltag = "span";
			}
		} else if (mFilterTagsMode == StyledlabelModule.INCLUDE_SPECIFIED_TAGS_ONLY) {
			if (!mFilteredTags.containsKey(ltag) && !(ltag.equals("html") || ltag.equals("body") || ltag.equals("style"))) {
				// Force it to be an innocuous span so that its contents are
				// still output.
				ltag = "span";
			}
		}

		if (ltag.equals("br")) {
			// We don't need to handle this. TagSoup will ensure that there's a
			// </br> for each <br> so we can safely emit the line breaks when we
			// handle the close tag.
		} else if (ltag.equals("p") || ltag.equals("div")) {

		} else if (ltag.equals("font")) {

		} else if (ltag.equals("a")) {

		} else if (ltag.equals("blockquote")) {

		} else if (ltag.length() == 2 && ltag.charAt(0) == 'h' && ltag.charAt(1) >= '1' && ltag.charAt(1) <= '6') {
			mSB.append("\n");
		} else if (ltag.equals("img")) {
			startImg(attributes);
		} else if (ltag.equals("ul")) {
			startList(false);
		} else if (ltag.equals("ol")) {
			startList(true);
		} else if (SIMPLE_TAGS.containsKey(ltag)) {
			// We don't need to track anything else.
		} else if (IGNORED_TAGS.containsKey(ltag)) {
			// Ignore the tag...
		} else {
			Util.e("Unknown start tag encountered: " + tag);
		}
	}

	private void handleEndTag(String tag) {
		String ltag = tag.toLowerCase();
		TagMarker marker = popTag(mSB, ltag);

		Boolean ignoreStyles = false;
		if (mFilterTagsMode == StyledlabelModule.EXCLUDE_SPECIFIED_TAGS) {
			if (mFilteredTags.containsKey(ltag)) {
				// Force it to be an innocuous span so that its contents are
				// still output.
				ltag = "span";
				ignoreStyles = true;
			}
		} else if (mFilterTagsMode == StyledlabelModule.INCLUDE_SPECIFIED_TAGS_ONLY) {
			if (!mFilteredTags.containsKey(ltag) && !(ltag.equals("html") || ltag.equals("body") || ltag.equals("style"))) {
				// Force it to be an innocuous span so that its contents are
				// still output.
				ltag = "span";
				ignoreStyles = true;
			}
		}

		if (ltag.equals("br")) {
			handleBr();
		} else if (ltag.equals("p")) {
			handleP(marker);
		} else if (ltag.equals("div")) {
			handleP(marker);
		} else if (ltag.equals("span")) {
			end(marker, ltag, new StyleSpan(Typeface.NORMAL));
		} else if (ltag.equals("b") || ltag.equals("strong")) {
			end(marker, ltag, new StyleSpan(Typeface.BOLD));
		} else if (ltag.equals("em") || ltag.equals("cite") || ltag.equals("dfn") || ltag.equals("i")) {
			end(marker, ltag, new StyleSpan(Typeface.ITALIC));
		} else if (ltag.equals("big")) {
			end(marker, ltag, new RelativeSizeSpan(1.25f));
		} else if (ltag.equals("small")) {
			end(marker, ltag, new RelativeSizeSpan(0.8f));
		} else if (ltag.equals("font")) {
			endFont(marker);
		} else if (ltag.equals("blockquote")) {
			handleP(marker);
			end(marker, ltag, new QuoteSpan());
		} else if (ltag.equals("tt") || ltag.equals("pre") || ltag.equals("code")) {
			end(marker, ltag, new TypefaceSpan("monospace"));
		} else if (ltag.equals("a")) {
			endA(marker);
		} else if (ltag.equals("u")) {
			end(marker, ltag, new UnderlineSpan());
		} else if (ltag.equals("strike") || ltag.equals("del") || ltag.equals("s")) {
			end(marker, ltag, new StrikethroughSpan());
		} else if (ltag.equals("sup")) {
			end(marker, ltag, new SuperscriptSpan());
		} else if (ltag.equals("sub")) {
			end(marker, ltag, new SubscriptSpan());
		} else if (ltag.equals("li")) {
			endLi(marker);
		} else if (ltag.equals("style")) {
			endStyle(marker);
		} else if (ltag.equals("ul") || ltag.equals("ol") || ltag.equals("img")) {
			// Ignore the tag, nothing needs to be done to close it.
		} else if (tag.length() == 2 && ltag.charAt(0) == 'h' && ltag.charAt(1) >= '1' && ltag.charAt(1) <= '6') {
			handleP(marker);
			endHeader(marker, Integer.parseInt(Character.toString(ltag.charAt(1))));
		} else if (IGNORED_TAGS.containsKey(ltag)) {
			// Ignore the tag...
		} else {
			Util.e("Unknown end tag encountered: " + tag);
		}

		if (!ignoreStyles)
			applyStylesToTag(ltag, marker);
	}

	private void handleP(TagMarker tm) {
		int len = mSB.length();

		if (len >= 1 && mSB.charAt(len - 1) == '\n') {
			if (len >= 2 && mSB.charAt(len - 2) == '\n') {
				return;
			}

			mSB.append("\n");
			return;
		}

		if (len != 0) {
			mSB.append("\n\n");
		}
	}

	private void handleBr() {
		mSB.append("\n");
	}

	private void end(TagMarker tm, String kind, Object repl) {
		int end = mSB.length();
		int start = tm.getStart();
		if (start != end) {
			mSB.setSpan(repl, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	private boolean mListIsOrdered;
	private int mListCount;

	private void startList(boolean isOrdered) {
		mListIsOrdered = isOrdered;
		mListCount = 0;
	}

	private void endLi(TagMarker tm) {
		int end = mSB.length();

		int start = tm.getStart();

		if (start != end) {
			mSB.append('\n');
			Object span;
			if (mListIsOrdered) {
				span = new NumberedSpan(++mListCount);
			} else {
				span = new BulletSpan();
			}
			mSB.setSpan(new LeadingMarginSpan.Standard(12, 12), start, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			mSB.setSpan(span, start, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	private void endStyle(TagMarker tm) {
		int end = mSB.length();
		int start = tm.getStart();

		if (start != end) {
			parseStyleBlock(mSB.toString().substring(start, end));
			mSB.delete(start, end);
		}
	}

	private void parseStyleBlock(String css) {
		int braceLevel = 0, braceMarker = 0;
		String selector = null;
		for (int i = 0, l = css.length(); i < l; i++) {
			char c = css.charAt(i);
			// An opening brace! It could be the start of a new rule, or it
			// could be a nested brace.
			if (c == '{') {
				// If we start a new rule...
				if (braceLevel == 0) {
					// Grab the selector (we'll process it in a moment)
					selector = css.substring(braceMarker, i - 1);
					// And mark our position so we can grab the rule's CSS when
					// it is closed
					braceMarker = i + 1;
				}
				// Increase the brace level.
				braceLevel += 1;
			}
			// A closing brace!
			else if (c == '}') {
				// If we finished a rule...
				if (braceLevel == 1) {
					String rule = css.substring(braceMarker, i - 1);
					if (selector != null)
						addStyleRule(rule, selector);
					braceMarker = i + 1;
				}
				braceLevel = Math.max(braceLevel - 1, 0);
			}
		}
	}

	private void addStyleRule(String rule, String selectors) {
		String[] split = selectors.split(",");
		for (String selector : split) {
			String cleanSelector = selector.trim();
			String cleanRule = rule.trim();
			if (mStyles.containsKey(cleanSelector)) {
				mStyles.put(cleanSelector, mStyles.get(cleanSelector) + "; " + cleanRule);
			} else {
				mStyles.put(cleanSelector, cleanRule);
			}
		}
	}

	private void applyStylesToTag(String ltag, TagMarker tm) {
		// We are going to combine all the relevant styles for this tag.
		// (Note that when styles are applied, the later styles take precedence,
		// so the order in which we grab them matters!)
		ArrayList<String> styles = new ArrayList<String>();

		// Get based on element
		if (mStyles.containsKey(ltag)) {
			styles.add(mStyles.get(ltag));
		}

		// Get based on class(es)
		String classString = tm.getAttributes().getValue("class");
		if (classString != null) {
			String[] classes = classString.split(" ");
			for (String clss : classes) {
				String classRule = "." + clss;
				if (mStyles.containsKey(classRule)) {
					styles.add(mStyles.get(classRule));
				}
			}
		}

		// Get based on id
		String idString = tm.getAttributes().getValue("id");
		if (idString != null) {
			String idRule = "#" + idString;
			if (mStyles.containsKey(idRule)) {
				styles.add(mStyles.get(idRule));
			}
		}

		// Get based on inline styles
		String styleString = tm.getAttributes().getValue("style");
		if (styleString != null) {
			styles.add(styleString);
		}

		// Now apply the CSS!
		for (String style : styles) {
			String[] pairs = style.split(";");
			for (String rawPair : pairs) {
				String[] pair = rawPair.split(":");
				if (pair.length == 0 || rawPair.length() == 0) {
					continue;
				}
				if (pair.length != 2) {
					Util.e("Skipping poorly formed CSS pair for " + ltag + ": " + pair.toString());
					continue;
				}
				String key = pair[0].trim().toLowerCase();
				String value = pair[1].trim();
				translateKeyValueInToSpannable(tm, key, value);
			}
		}
	}

	private void translateKeyValueInToSpannable(TagMarker tm, String key, String value) {
		IProperty property = PropertyManager.getInstance().getProperty(key);
		if (property == null) {
			Util.e("Unsupported CSS property found: " + key + ": " + value);
		} else {
			ParcelableSpan span = property.getSpan(mSB, value);
			if (span != null) {
				mSB.setSpan(span, tm.getStart(), mSB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	private void startImg(Attributes attributes) {
		String src = attributes.getValue("", "src");
		Drawable d = null;

		if (mImageGetter != null) {
			d = mImageGetter.getDrawable(src);
		}

		if (d == null) {
			mSB.setSpan(new BackgroundColorSpan(0), mSB.length(), mSB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return;
		}

		int len = mSB.length();
		mSB.append("\uFFFC");

		mSB.setSpan(new ImageSpan(d, src), len, mSB.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	private void endFont(TagMarker tm) {
		int end = mSB.length();
		int start = tm.getStart();
		if (start != end) {
			String color = tm.getAttributes().getValue("", "color");
			String face = tm.getAttributes().getValue("", "face");
			String size = tm.getAttributes().getValue("", "size");
			if (!TextUtils.isEmpty(color)) {
				int c = Util.getHtmlColor(color);
				if (c != -1) {
					mSB.setSpan(new ForegroundColorSpan(c | 0xFF000000), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			if (face != null) {
				mSB.setSpan(new TypefaceSpan(face), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (size != null) {
				mSB.setSpan(Util.sizeToSpan(size), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	private void endA(TagMarker tm) {
		int end = mSB.length();
		int start = tm.getStart();
		if (start != end) {
			String href = tm.getAttributes().getValue("", "href");
			if (href != null) {
				mSB.setSpan(new URLSpan(href), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	private void endHeader(TagMarker tm, int i) {
		int start = tm.getStart();
		int end = mSB.length();

		// Back off to change only the text, not the blank line.
		while (end > start && mSB.charAt(end - 1) == '\n') {
			end--;
		}

		if (start != end) {
			mSB.setSpan(new RelativeSizeSpan(HEADER_SIZES[i]), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			mSB.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		handleStartTag(localName, attributes);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		handleEndTag(localName);
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		StringBuilder sb = new StringBuilder();

		/*
		 * Ignore whitespace that immediately follows other whitespace; newlines count as spaces.
		 */

		for (int i = 0; i < length; i++) {
			char c = ch[i + start];

			if (c == ' ' || c == '\n') {
				char pred;
				int len = sb.length();

				if (len == 0) {
					len = mSB.length();

					if (len == 0) {
						pred = '\n';
					} else {
						pred = mSB.charAt(len - 1);
					}
				} else {
					pred = sb.charAt(len - 1);
				}

				if (pred != ' ' && pred != '\n') {
					sb.append(' ');
				}
			} else {
				sb.append(c);
			}
		}

		mSB.append(sb);
	}

	public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
	}

	public void processingInstruction(String target, String data) throws SAXException {
	}

	public void skippedEntity(String name) throws SAXException {
	}

}