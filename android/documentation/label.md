# Ti.StyledLabel.Label

## Description

Displays html and css.

## Properties

### filteredTags

Controls which tags are included or excluded from your output, based on what you specify as the
"filteredTagsMode".

### filteredTagsMode

Controls if the tags specified in "filteredTags" are the only HTML tags allowed, or if they are the only
tags that are excluded.

Set this property to one of the two constants:

* Ti.StyledLabel.INCLUDE_SPECIFIED_TAGS_ONLY
* Ti.StyledLabel.EXCLUDE_SPECIFIED_TAGS

### font

Custom font of the label text. The font should be stored project's **Resources/fonts/** directory.

#### Example

	font: { 
		fontSize: 24,
		fontFamily: myFontName
	}

### color
	
Color of the label text, as a color name or hex triplet.

For information about color values, see the "Colors" section of [Titanium.UI](http://docs.appcelerator.com/platform/latest/#!/api/Titanium.UI). 

### html

Specifies the HTML that should be loaded in to the label. Note that this HTML will NOT simply
run in a web view! Therefore, you can only use a subset of HTML and CSS -- no JavaScript!

The following HTML conventions are supported:

* img (both remote and local -- remote images will be lazy loaded as they are viewed)
* blockquote
* tt, pre, code
* a (use the "click" events to handle what happens when the user touches a link)
* b and strong
* i, em, cite, and dfn
* ul, ol, and li
* del and strike
* u
* sup and sub
* h1-6
* big
* small
* font (with size, face, and/or color attributes)
* p and br

You also have a couple of options for styling these elements:

* inline CSS
* style tags with simple CSS selectors

The following CSS selectors are supported:

* id based (#rule)
* class based (.red, .green)
* tag based (span, div, p)

Note that you cannot chain selectors in a single rule (like "div .red"), but you can
combine them (like ".red, .blue { }).

Also note that you do not have access to the full CSS specification. The following properties
are supported:

* color
* background-color
* font-weight
* font-size
* text-indent
* text-size
* text-align

## Events

### click

Occurs when a link is touched. Receives a dictionary with the following properties:

* string url: The URL of the link the user touched.
