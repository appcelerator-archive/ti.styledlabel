# Change Log
<pre>
v2.0.3  [MOD-1904] Changes createView method in LabelProxy.java to make the label unclickable (Resolves :Click event fires twice)

v2.0.1  [MOD-944] Move to open source repo and build with 2.1.3.GA
	
v2.0    Upgraded to module api version 2 for 1.8.0.1

v1.3    iOS and Android Module Parity: [MOD-284]
		- iOS: BREAKING CHANGE. A Styled Label is no longer a scroll view, just like on Android; place it in a scroll view to achieve the previous behavior. Check out the example to see how.
		- Android: Fixed bug causing the styled label to disappear when placed in a table view [MOD-267]
		- iOS: Fixed contention issue causing CPU spikes and long load times on iOS versions prior to 5.0 [MOD-310]

v1.0    Initial Release
