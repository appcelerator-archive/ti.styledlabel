# Change Log
<pre>
v1.6.0  TIMOB-17928 Updated to build for 64-bit

v1.5.4  Fixed click event not firing for non-HTTP links [MOD-1076]
	
v1.5.3  Move to open source repo and build with 2.1.3.GA [MOD-944]

v1.5.2  Resolved additional issue with auto height in Titanium Mobile 1.8.2 (should use Ti.UI.SIZE for Titanium Mobile 2.x.x)

v1.5.1	Fixed the auto height regression with Titanium Mobile 1.8.2 [MOD-723]

v1.5	Rewrite underlying engine to use a light UIWebView for way better performance [MOD-573]
		- Updated example to demonstrate loading 200 tweets
		- Fix compilation and layout issues with Titanium Mobile 2.0.1 [MOD-647]
		- Added "Common Problems and Solutions" section to documentation
		- Removed filtered tags properties due to lack of use

v1.4    [MOD-557] Removed 'all_load' from module.xcconfig for compatibility with Titanium SDK 2.0.0

v1.3	iOS and Android Module Parity: [MOD-284]
		- iOS: BREAKING CHANGE. A Styled Label is no longer a scroll view, just like on Android; place it in a scroll view to achieve the previous behavior. Check out the example to see how.
		- Android: Fixed bug causing the styled label to disappear when placed in a table view [MOD-267]
		- iOS: Fixed contention issue causing CPU spikes and long load times on iOS versions prior to 5.0 [MOD-310]
		- iOS: The background of the styled label view is now transparent.

v1.2	[MOD-255] Fixed issue with namespacing of certain methods.

v1.1    [MOD-234] Calculate correct size when 'auto' is specified for height or width

v1.0    Initial Release
