#import <Foundation/Foundation.h>



@protocol FileDownloaderProtocol <NSObject>

@required
-(void) fileDownloadedForURL:(NSString *) url atPath:(NSURL *) path;
-(void) fileDownloadFailedForURL:(NSString *) url withError:(NSError *) error;

@end

// Downloads a file from a URL
// The downloaded file would be saved under 'DownloadedFiles' folder in either
// Document or Caches directory, based on Dev/Prod settings

// The files would be cached based on the URL provieded for download. Any request for the
// same URL would result in overwrite of the same file
// 
@interface FileDownloadHelper : NSObject 


@property(nonatomic, weak) id delegate;

// Makes a GET request. 
-(void) downloadFileFromURL:(NSString *) url saveAtPath:(NSURL *) saveAtPath;
// Will make a POST request with 'withPostParameters' as the HttpBody
// 'withPostParameters' should be nil if the caller just wants to make a post request
-(void) downloadFileFromURL:(NSString *) url withPostParameters:(NSString *) postBody saveAtPath:(NSURL *) saveAtPath;

@end
