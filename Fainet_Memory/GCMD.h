//
//  GCMD.h
//  Fainet_Memory
//
//  Created by YTT1 on 2016/11/8.
//  Copyright © 2016年 YTT1. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@protocol GCMDOPTION <NSObject>

@property Boolean Status;
@end

@interface GCMD : NSObject <GCMDOPTION>

@property  float Width;
@property  float Height;
@property  float Personheight;
-(void) ViewTransitionSetting;
-(NSString *) tmp;
@end


#define RGBrange 255
#define UUID_SERVICE_GBC                     @"6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
#define circlemarc(degress) ((3.14*degress)/180)
#define degreesToRadians(x) ((x) * (M_PI / 180.0))


#define UUID_DEVICE_INFORMATION_SERVICE_GBC  @"180A"
#define UUID_TX_CHARACTERISTIC_GBC           @"6E400003-B5A3-F393-E0A9-E50E24DCCA9E"
#define UUID_RX_CHARACTERISTIC_GBC           @"6E400002-B5A3-F393-E0A9-E50E24DCCA9E"


