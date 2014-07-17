#import <Foundation/Foundation.h>

typedef void (^ConnectionCompletionBlock)(id response, NSError *error);

@protocol NSURLConnectionHelperProtocol <NSObject>
- (void)didLoadData:(NSData *)responseData forRequest:(NSURLRequest *)request;
- (void)didFailToLoadWithError:(NSError *)error forRequest:(NSURLRequest *)request;
@end

@interface NSURLConnectionHelper : NSObject
@property(nonatomic, weak) id<NSURLConnectionHelperProtocol> delegate;

- (void)getDataForRequest:(NSURLRequest *)request;
- (void)getDataForRequest:(NSURLRequest *)request completionBlock:(ConnectionCompletionBlock)completionBlock;
@end
