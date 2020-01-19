package com.Lizi.MyHttp.NettyServer;

import com.Lizi.MyHttp.Util.outUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderNames.EXPIRES;

public class Response {

    private ChannelHandlerContext ctx;

    public Response(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void write(String out,HttpResponseStatus rescode){
        try {
            if (out == null || out.length() == 0) {
                return;
            }

            ByteBuf buf= Unpooled.wrappedBuffer(out.getBytes(CharsetUtil.UTF_8));
            // 设置 http协议及请求头信息
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                    // 设置http版本为1.1
                    HttpVersion.HTTP_1_1,
                    // 设置响应状态码
                    rescode,
                    // 将输出值写出 编码为UTF-8
                    buf);
            HttpHeaders headers = response.headers();
            // 设置连接类型
            headers.set(CONTENT_TYPE, "application/json");
            //允许跨域访问
            headers.set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            headers.set(ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept");
            headers.set(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST");
            // 设置请求头长度
            headers.set(CONTENT_LANGUAGE, response.content().readableBytes());
            // 设置超时时间为5000ms
            headers.set(EXPIRES, 5000);
            // 当前是否支持长连接
//            if (HttpUtil.isKeepAlive(r)) {
//                // 设置连接内容为长连接
//                headers.set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//            }
            ctx.writeAndFlush(response);
        }catch (Exception e){
            outUtil.outErr("响应出错了");
            ctx.close();
        }
    }

}
