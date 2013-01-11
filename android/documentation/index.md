# Ti.StyledLabel Module

## Description

Gives you the power of HTML and CSS without the weight of a WebView.

## Getting Started

View the [Using Titanium Modules](http://docs.appcelerator.com/titanium/latest/#!/guide/Using_Titanium_Modules) document for instructions on getting
started with using this module in your application.

Note that there may be two versions of this module available to you, one for use with pre-1.8.0.1 SDKs and one for use with 1.8.0.1 or newer.
In your tiapp.xml file, make sure that you specify the version of the module that corresponds to the version of Titanium Mobile SDK that you are targeting.
For Appcelerator modules, specify the 1.X version of the module if building for versions of Titanium Mobile SDK prior to 1.8.0.1 and specify the 2.X version of the module if
building for versions of Titanium Mobile SDK 1.8.0.1 or newer.

## Accessing the Ti.StyledLabel Module

To access this module from JavaScript, you would do the following:

	var StyledLabel = require('ti.styledlabel');

## Functions

### [Ti.StyledLabel.Label][] Ti.StyledLabel.createLabel({...})

Creates a [Ti.StyledLabel.Label][].

## Constants

### Ti.StyledLabel.INCLUDE_SPECIFIED_TAGS_ONLY

Use with the [Ti.StyledLabel.Label][]'s "filteredTagsMode" property. Any tag that is
not in the array of filteredTags will ignored in the output. Any raw text will still
be output, but the tag will have no influence on the formatting of the text.

### Ti.StyledLabel.EXCLUDE_SPECIFIED_TAGS

Use with the [Ti.StyledLabel.Label][]'s "filteredTagsMode" property. Any tag that is
in the array will ignored in the output. Any raw text will still be output, but the tag
will have no influence on the formatting of the text.

## Usage

See example.

## Author

Dawson Toth

## Module History

View the [change log](changelog.html) for this module.

## Feedback and Support

Please direct all questions, feedback, and concerns to [info@appcelerator.com](mailto:info@appcelerator.com?subject=Android%20StyledLabel%20Module).

## License

Copyright(c) 2010-2013 by Appcelerator, Inc. All Rights Reserved. Please see the LICENSE file included in the distribution for further details.

[Ti.StyledLabel.Label]: label.html