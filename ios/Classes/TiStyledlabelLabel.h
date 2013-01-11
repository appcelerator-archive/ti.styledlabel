/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
 
#import "TiUIView.h"

@interface TiStyledlabelLabel : TiUIView<UIWebViewDelegate> {
    UIWebView* _web;
    NSString* _html;
    float _contentHeight;
}

-(void)setHtml_:(NSString *)html;
-(void)createView;
-(float)currentContentHeight;

@end
