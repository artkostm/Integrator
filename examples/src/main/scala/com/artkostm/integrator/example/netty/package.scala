package com.artkostm.integrator.example

package object netty {
  
  /**
   * 
   * This example applies only to the direct transmission of a file’s contents, excluding any
processing of the data by the application. In cases where you need to copy the data
from the file system into user memory, you can use ChunkedWriteHandler, which provides
support for writing a large data stream asynchronously without incurring high
memory consumption.

   *  FileInputStream in = new FileInputStream(file);
      FileRegion region = new DefaultFileRegion(
      in.getChannel(), 0, file.length());
      channel.writeAndFlush(region).addListener(
        new ChannelFutureListener() {
          @Override
          public void operationComplete(ChannelFuture future)
            throws Exception {
            if (!future.isSuccess()) {
              Throwable cause = future.cause();
              // Do something
          	}
        	}
      });
      
    ChunkedFile - Fetches data from a file chunk by chunk, for use when your platform doesn’t
support zero-copy or you need to transform the data
    ChunkedNioFile - Similar to ChunkedFile except that it uses FileChannel
    ChunkedStream - Transfers content chunk by chunk from an InputStream
    ChunkedNioStream - Transfers content chunk by chunk from a ReadableByteChannel
    
    
    HTTP 1.0 client does not support chunked response
   */
}