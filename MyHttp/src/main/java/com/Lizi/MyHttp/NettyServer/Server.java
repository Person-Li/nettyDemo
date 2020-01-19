package com.Lizi.MyHttp.NettyServer;

import com.Lizi.MyHttp.Annot.After;
import com.Lizi.MyHttp.Annot.Before;
import com.Lizi.MyHttp.Factory.RestApiFactory;
import com.Lizi.MyHttp.Util.outUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends ChannelInboundHandlerAdapter {
    RestApiFactory beanFactory= RestApiFactory.getBeanFactory();
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest=(FullHttpRequest)msg;
        try {
            Request request = new Request(fullHttpRequest);
            String uri = request.getURI();        //获取请求路由
            if (uri.equals("/favicon.ico")) {return;}         //不解析.ico请求
            Response response = new Response(ctx);
            if (request.getMethodName().equals("GET")) {
                Method m = beanFactory.getMethod.get(uri);
                callbackfunction(request, response, m);
            } else if (request.getMethodName().equals("POST")) {
                Method m = beanFactory.postMethod.get(uri);
                callbackfunction(request, response, m);
            } else {
                response.write("请求方式不合法", HttpResponseStatus.FORBIDDEN);
            }
        }finally {
            fullHttpRequest.release();
        }

    }

    private void callbackfunction(Request request, Response response, Method m) throws Exception {
        if(m!=null)
        {
            Object a= m.invoke(beanFactory.component.get(m.getDeclaringClass().getName()),request.getParam());
            response.write((String)a, HttpResponseStatus.OK);
//            if(a instanceof String) {
//                response.write((String)a, HttpResponseStatus.OK);
//            }else {
//                outUtil.outErr("接口返回值不能为非String类型");
//                response.write("类型转换出错了",HttpResponseStatus.INTERNAL_SERVER_ERROR);
//            }
        }else {
            response.write("请求地址错误",HttpResponseStatus.NOT_FOUND);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        outUtil.outYellow("exceptionCaught()方法被触发,可能出现了问题");
        Channel channel = ctx.channel();

        if(channel.isActive())ctx.close();
//        DefaultFullHttpResponse response = new DefaultFullHttpResponse( HttpVersion.HTTP_1_1,HttpResponseStatus.INTERNAL_SERVER_ERROR);
//        ctx.writeAndFlush(response);
    }
}
