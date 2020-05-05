//
//  RNZendeskChat.m
//  Tasker
//
//  Created by Jean-Richard Lai on 11/23/15.
//  Copyright © 2015 Facebook. All rights reserved.
//

#import "RNZendeskChatModule.h"
#import <ZDCChat/ZDCChat.h>

@implementation RNZendeskChatModule

RCT_EXPORT_MODULE(RNZendeskChatModule);

RCT_EXPORT_METHOD(setVisitorInfo:(NSDictionary *)options) {
  [ZDCChat updateVisitor:^(ZDCVisitorInfo *visitor) {
    if (options[@"name"]) {
      visitor.name = options[@"name"];
    }
    if (options[@"email"]) {
      visitor.email = options[@"email"];
    }
    if (options[@"phone"]) {
      visitor.phone = options[@"phone"];
    }
    visitor.shouldPersist = [options[@"shouldPersist"] boolValue] || NO;
  }];
}

RCT_EXPORT_METHOD(startChat:(NSDictionary *)options) {
  [self setVisitorInfo:options];

  if (options[@"hideOverlay"]) {
    [[[ZDCChat instance] overlay] setEnabled:NO];
  }
  dispatch_sync(dispatch_get_main_queue(), ^{
    [ZDCChat startChat:^(ZDCConfig *config) {
      if (options[@"department"]) {
        config.department = options[@"department"];
      }
      if (options[@"tags"]) {
        config.tags = options[@"tags"];
      }
      config.preChatDataRequirements.name       = ZDCPreChatDataRequired;
      config.preChatDataRequirements.department = ZDCPreChatDataRequiredEditable;
      config.preChatDataRequirements.email      = ZDCPreChatDataRequired;
      config.preChatDataRequirements.phone      = ZDCPreChatDataRequired;
      config.preChatDataRequirements.message    = ZDCPreChatDataRequired;
      config.emailTranscriptAction              = ZDCEmailTranscriptActionNeverSend;
    }];
  });
}

RCT_EXPORT_METHOD(init:(NSString *)zenDeskKey) {
  [ZDCChat initializeWithAccountKey:zenDeskKey];
}

@end
