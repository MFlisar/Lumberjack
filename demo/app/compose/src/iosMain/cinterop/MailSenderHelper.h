#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>

@interface MailSenderHelper : NSObject <MFMailComposeViewControllerDelegate>
- (void)sendMailWithReceiver:(NSString *)receiver attachments:(NSArray<NSString *> *)attachments;
@end