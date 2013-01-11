# Ti.StyledLabel Module

## Description

Gives you the power of HTML and CSS without the full weight of a WebView.

Lightly wraps around a UIWebView to make it easy and quick for you to display HTML. This is intended for displaying
small snippets of HTML, like Tweets, to your users. It works best if you limit the number of Styled Label's you create.

## Solutions to Common Scenarios

### Table of Tweets

Problem: You need to display a lot of tweets (or other HTML) in a table in your app.

BAD Solution: Create one Styled Label per tweet. So if you have 200 tweets, you end up with 200 Styled Labels.

GOOD Solution: Create ONE Styled Label. Render all of the tweets together at once.

### Other Scenarios

Are you facing a performance or functional issue with the Styled Label? Let us know and we will come up with a solution
together!

Send an email to [info@appcelerator.com](mailto:info@appcelerator.com?subject=iOS%20StyledLabel%20Module) RE: StyledLabel.

## Getting Started

View the [Using Titanium Modules](http://docs.appcelerator.com/titanium/latest/#!/guide/Using_Titanium_Modules) document for instructions on getting
started with using this module in your application.

## Accessing the Ti.StyledLabel Module

To access this module from JavaScript, you would do the following:

	var StyledLabel = require('ti.styledlabel');

## Functions

### [Ti.StyledLabel.Label][] Ti.StyledLabel.createLabel({...})

Creates a [Ti.StyledLabel.Label][].

## Usage

See example.

## Author

Dawson Toth

## Module History

View the [change log](changelog.html) for this module.

## Feedback and Support

Please direct all questions, feedback, and concerns to [info@appcelerator.com](mailto:info@appcelerator.com?subject=iOS%20StyledLabel%20Module).

## License

Copyright(c) 2010-2013 by Appcelerator, Inc. All Rights Reserved. Please see the LICENSE file included in the distribution for further details.

[Ti.StyledLabel.Label]: label.html