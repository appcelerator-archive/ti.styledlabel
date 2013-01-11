var StyledLabel = require('ti.styledlabel');

/**
 * We are going to demonstrate how to use the Ti.StyledLabel module to load up a list of tweets.
 */
var win = Ti.UI.createWindow({
    backgroundColor: '#fff'
});
var container = Ti.UI.createScrollView({
    scrollType: 'vertical',
    contentHeight: 'auto',
    disableBounce: false,
    horizontalBounce: false,
    verticalBounce: true,
    showVerticalScrollIndicator: true
});
var label = StyledLabel.createLabel({
    height: Ti.UI.SIZE || 'auto',
    top: 5, right: 5, bottom: 5, left: 5,
    html: '<center>Loading, please wait.</center>'
});
label.addEventListener('click', function (evt) {
    if (evt.url) {
        alert('You clicked ' + evt.url);
    }
});
container.add(label);
win.add(container);
win.open();

var hr = '<hr />';
var baseCSS = '<style type="text/css">\
        body {\
            font-family: HelveticaNeue;\
            font-size: 14px;\
            color: #555;\
        }\
        a {\
            color: #099;\
        }\
        img {\
            float: left;\
        }\
        hr {\
            clear: both;\
            color: #ccc;\
            background-color: #ccc;\
            margin: 5px 0 10px;\
            border: 0;\
            width: 100%;\
            height: 1px;\
        }\
        div {\
            padding-left: 60px;\
        }\
    </style>';

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
            var html = [ baseCSS ];
            var tweets = JSON.parse(this.responseText);
            if (!tweets || !tweets.length) {
                alert('No tweets found!');
                return;
            }
            for (var key in tweets) {
                var tweet = tweets[key];
                var image = '<img src="' + tweet.user.profile_image_url + '" width=48 height=48 />';
                html.push(image + '<div>' + parseTweetToHTML(tweet.text) + '</div>' + hr);
            }
            label.html = html.join('\n');
        }
        catch (err) {
            alert(err);
        }
    },
    onerror: function (evt) {
        alert(evt);
    }
});
http.open('GET', 'http://api.twitter.com/1/statuses/user_timeline.json?' +
    'screen_name=appcelerator&' +
    'include_rts=1&' +
    'count=200'); // That's right, 200 tweets.
http.send();