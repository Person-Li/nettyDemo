package com.Lizi.MyHttp.NettyServer;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpDownloadHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private String filePath="D:\\下载\\";
    public HttpDownloadHandler() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if (uri.startsWith("/download") && request.method().equals(HttpMethod.GET)) {
            System.out.println("下载请求");
            Response res=new Response(ctx);
            File file = new File(filePath);
            try {
                final RandomAccessFile raf = new RandomAccessFile(file, "r");
                long fileLength = raf.length();
                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
                response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
                ctx.write(response);
                ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationComplete(ChannelProgressiveFuture future)
                            throws Exception {
                        raf.close();

//                        System.out.println("下载完成");
                    }

                    @Override
                    public void operationProgressed(ChannelProgressiveFuture future,
                                                    long progress, long total) throws Exception {
//                        System.out.println(progress+"/"+total);
                    }
                });
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } catch (FileNotFoundException e) {
                //log.warn("file {} not found", file.getPath());
                //generalResponse = new GeneralResponse(HttpResponseStatus.NOT_FOUND, String.format("file %s not found", file.getPath()), null);
                res.write("file %s not found",HttpResponseStatus.NOT_FOUND);
            } catch (IOException e) {
//                log.warn("file {} has a IOException: {}", file.getName(), e.getMessage());
//                generalResponse = new GeneralResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, String.format("读取 file %s 发生异常", filePath), null);
//                ResponseUtil.response(ctx, request, generalResponse);
                res.write("读取 file %s 发生异常",HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            ctx.fireChannelRead(request);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
//        log.warn("{}", e);
        ctx.close();

    }
}

