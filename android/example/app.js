/**
 * We are going to demonstrate how to use the Ti.StyledLabel module
 * in two different scenarios:
 *
 * 1. First, by loading in some HTML from text.html and displaying it to you.
 * 2. Second, by loading in some tweets, formatting them, and displaying them.
 */
var win = Ti.UI.createWindow({
    backgroundColor: '#fff'
});
var scroll = Ti.UI.createScrollView({
    contentHeight: 'auto',
    layout: 'vertical'
});
win.add(scroll);

var StyledLabel = require('ti.styledlabel');

function linkClickListener(evt) {
    if (evt.url) {
        alert('You clicked ' + evt.url);
    }
}

var customFontLabel = StyledLabel.createLabel({
    html: '<h1>StyledLabel With Melissa</h1>',
    font: { fontFamily: 'Melissa', fontSize: 20}
});

scroll.add(customFontLabel);

var redLabel = StyledLabel.createLabel({
    html: '<h2>This is RED</h2>',
    color: 'red'
});

scroll.add(redLabel);

var greenLabel = StyledLabel.createLabel({
    html: '<h2>This is GREEN</h2>',
    color: 'green'
});

scroll.add(greenLabel);

/**
 * This is the first example. It loads some HTML from a file, sticks it in
 * the "html" property of our label, and shows it to the user. It also shows
 * to buttons that control the "filteredTags" and "filteredTagsMode" properties
 * of the label to let you limit what HTML is displayed to the user.
 */
var onlyAllowLinks = Ti.UI.createButton({
    title: 'Only Allow Links',
    height: 60
});
onlyAllowLinks.addEventListener('click', function () {
    label.filteredTags = ['a'];
    label.filteredTagsMode = StyledLabel.INCLUDE_SPECIFIED_TAGS_ONLY;
});
scroll.add(onlyAllowLinks);

var everythingButLinks = Ti.UI.createButton({
    title: 'Allow Everything But Links',
    height: 60
});
everythingButLinks.addEventListener('click', function () {
    label.filteredTags = ['a'];
    label.filteredTagsMode = StyledLabel.EXCLUDE_SPECIFIED_TAGS;
});
scroll.add(everythingButLinks);

var label = StyledLabel.createLabel({
    html: Ti.Filesystem.getFile('text.html').read().text,
    height: Ti.UI.SIZE || 'auto',
    backgroundColor: '#fff',
    borderColor: '#ccc', borderWeight: 1,
    bottom: -1
});
label.addEventListener('click', linkClickListener);
scroll.add(label);

/**
 * Our second example is a bit more complicated, but only just -- we're going to
 * grab some tweets from Twitter, parse them in to HTML, and then then show them
 * to the user.
 */
var baseCSS = '<style type="text/css">' +
    'a { color: #099; }' +
    '</style>';

function parseTweetToHTML(text) {
    var urlRegex = /((ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?)/gi;
    var hashTagRegex = /#([^ ]+)/gi;
    var mentionRegex = /@([^ ]+)/gi;
    return text
        .replace(urlRegex, '<a href="$1">$1</a>')
        .replace(hashTagRegex, '<a href="http://twitter.com/#!/search?q=%23$1">#$1</a>')
        .replace(mentionRegex, '<a href="http://twitter.com/#!/search?q=%40$1">@$1</a>');
}

var http = Ti.Network.createHTTPClient({
    onload: function () {
        try {
            var tweets = JSON.parse(this.responseText);
            if (!tweets || !tweets.length) {
                alert('No tweets found!');
                return;
            }
            for (var key in tweets) {
                var tweet = tweets[key];
                var tweetView = Ti.UI.createView({
                    height: Ti.UI.SIZE || 'auto',
                    backgroundColor: '#fff',
                    borderColor: '#ccc', borderWeight: 1
                });
                tweetView.add(Ti.UI.createImageView({
                    left: 5, top: 5,
                    width: 48, height: 48,
                    image: tweet.user.profile_image_url
                }));
                var label = StyledLabel.createLabel({
                    left: 70, top: 5, bottom: 5, right: 5,
                    height: Ti.UI.SIZE || 'auto',
                    html: baseCSS + '<span>' + parseTweetToHTML(tweet.text) + '</span>'
                });
                label.addEventListener('click', linkClickListener);
                tweetView.add(label);
                scroll.add(tweetView);
            }
        }
        catch (err) {
            alert(err);
        }
    },
    onerror: function (evt) {
        alert(evt);
    }
});
http.open('GET', 'http://api.twitter.com/1/statuses/user_timeline.json?screen_name=appcelerator&include_rts=1');
http.send();

win.open();