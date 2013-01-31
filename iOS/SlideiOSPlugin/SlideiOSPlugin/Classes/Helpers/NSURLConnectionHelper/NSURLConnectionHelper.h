#import <Foundation/Foundation.h>

@protocol NSURLConnectionHelperProtocol <NSObject>


// Gets called when the download completes successfully. The responseData object will
// contain the entire data downloaded from the target URL.
-(void)connectionDidFinishLoading:(NSURLConnection *)connection responseData:(NSData *) responseData;
// Gets called when the download completes successfully. The file would be saved
// at filePath 
-(void)connectionDidFinishLoading:(NSURLConnection *)connection filePath:(NSURL *) filePath;
// This gets called when the connection fails for any reason. 
-(void) requestFailedForConnection:(NSURLConnection *)connection error:(NSError *) error;

@end


// This is a wrapper around NSURLConnection class
// Objective of this class is to avoid copy-paste of same code at a bunch places
//
// Rather than creating a new NSURLConnection object and handling the delegate method
// yourself, any class in Neato app should use this class to get stuff from web.
//
// When using this class, you will ne notifed of only two events - when the download
// fails and when the download completes successfully.
// TODO: Add methods to support larger files
// TODO: Add protocol method to notify the caller of ongoing download
// TODO: May be addd authentication support
@interface NSURLConnectionHelper : NSObject

@property(nonatomic, weak) id delegate;
-(NSURLConnection *) getDataForRequest:(NSURLRequest *) request;
-(NSURLConnection *) downloadDataForRequest:(NSURLRequest *) request andSaveAtPath:(NSURL *) path;


@end
