/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiStyledlabelLabelProxy.h"
#import "TiUtils.h"

@implementation TiStyledlabelLabelProxy

-(void)viewDidAttach
{
    [(TiStyledlabelLabel*)[self view] createView];
}

// The following is to support the new layout in TiSDK
-(CGFloat)contentHeightForWidth:(CGFloat)value
{
    float height = [((TiStyledlabelLabel*)[self view]) currentContentHeight];
    if (height > 1) {
        return height;
    }
	return 1;
}

// The following is to support the old layout in prior versions of TiSDK
-(CGFloat)autoHeightForWidth:(CGFloat)suggestedWidth
{
    return [self contentHeightForWidth:suggestedWidth];
}

@end
