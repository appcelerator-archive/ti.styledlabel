/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiStyledlabelLabel.h"
#import "TiApp.h"
#import "TiBase.h"
#import "TiUtils.h"

@interface TiStyledlabelLabel (Private)

-(void)updateTextViewsHtml;

@end

@implementation TiStyledlabelLabel

#pragma mark -
#pragma mark Initialization and Memory Management

- (id)init {
	if ((self = [super init])) {
	}
	return self;
}

-(void)dealloc
{
    RELEASE_TO_NIL(_html);
    RELEASE_TO_NIL(_web);
	[super dealloc];
}

#pragma mark -
#pragma mark View management

-(UIWebView*)web {
    if (!_web) {
        _web = [[UIWebView alloc] initWithFrame:CGRectMake(0, 0, 320, 1)];
        UIView *v = [[_web subviews] lastObject];
        if([v isKindOfClass:[UIScrollView class]])
        {
            [v setScrollEnabled:NO];
        }
        _web.delegate = self;
        [self addSubview:_web];
    }
    return _web;
}

-(void)createView
{
    [self web];
}


-(void)frameSizeChanged:(CGRect)frame bounds:(CGRect)bounds
{
    [TiUtils setView:[self web] positionRect:bounds];
    if (_html != nil) {
        [[self web] loadHTMLString:_html baseURL:nil];
    }
    [super frameSizeChanged:frame bounds:bounds];
}

-(CGFloat)autoHeightForWidth:(CGFloat)value
{
	return _contentHeight;
}
 
-(CGFloat)autoWidthForWidth:(CGFloat)value
{
	return value;
}


#pragma mark -
#pragma mark Public APIs

-(float)currentContentHeight
{
    return _contentHeight;
}

-(void)setHtml_:(NSString *)html
{
    NSString* head =
    @"<meta name=viewport content=\"user-scalable=0\" /><style type=text/css>body{ margin: 0; padding: 0 }</style>";
    
    NSString* onload =
    @"<br clear=all/><script type=text/javascript>\
    window.onload = function() { window.location.href = 'ready://' + document.body.offsetHeight; };\
    </script>";
    
    RELEASE_TO_NIL(_html);
    _html = [[NSString stringWithFormat:@"%@%@%@", head, html, onload] retain];
    
    [[self web] loadHTMLString:_html baseURL:nil];
}

#pragma mark -
#pragma mark Delegate

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    NSURL *url = [request URL];
    if (navigationType == UIWebViewNavigationTypeOther) {
        if ([[url scheme] isEqualToString:@"ready"]) {
            _contentHeight = [[url host] floatValue];
            [((TiViewProxy*)[self proxy]) willEnqueue];
            [[((TiViewProxy*)[self proxy]) parent] willChangeSize];
            return NO;
        }
    }
    
    if ([url host] == nil) {
        return YES;
    }
    
    [self.proxy fireEvent:@"click" withObject:[[NSDictionary alloc] initWithObjectsAndKeys:
                                               [url absoluteString], @"url",
                                               nil]];
    return NO;
}

- (void)webViewDidStartLoad:(UIWebView *)webView
{
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
}

@end
