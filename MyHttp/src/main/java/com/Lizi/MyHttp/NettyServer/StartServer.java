package com.Lizi.MyHttp.NettyServer;

import com.Lizi.MyHttp.Factory.RestApiFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import com.Lizi.MyHttp.Util.outUtil;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.ResourceBundle;


public class StartServer {
   public StartServer(String configname) throws InterruptedException {
       ResourceBundle config=null;
        try {
           config=readProper(configname);                //读取配置文件
        }catch (Exception e){
            outUtil.outErr("未在resources目录下找到"+configname+".properties配置文件,服务器启动失败");
            return;
        }

        String packageName=null;
        Integer port=null;
        Integer maxContentLength=null;
        try {
            packageName= config.getString("httpServer.httpPackage");
        }catch (Exception e){
            outUtil.outYellow("未指定Rest接口所在包:httpServer.httpPackage，默认扫描全部文件");
        }
        RestApiFactory.getBeanFactory(packageName);                   //加载Annotation标注的函数

        outUtil.outBlue("服务器启动中...");
        try {
            port=Integer.parseInt(config.getString("httpServer.port"));
        }catch (Exception e){
            outUtil.outErr("未配置服务器端口:httpServer.port,启动失败");
            return;
        }
        try {
            maxContentLength=Integer.parseInt(config.getString("httpServer.maxContentLength"));
            outUtil.outBlue("连接数据长度为:"+maxContentLength+"byte");
        }catch (Exception e){
            maxContentLength=3072;
            outUtil.outYellow("未配置短连接数据长度:httpServer.maxContentLength,设置为默认长度"+maxContentLength+" byte");
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            Integer finalMaxContentLength = maxContentLength;
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpRequestDecoder());
//                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new HttpContentCompressor());
                            // 新增ChunkedHandler，主要作用是支持异步发送大的码流（例如大文件传输），但是不占用过多的内存，防止发生java内存溢出错误
//                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            ch.pipeline().addLast(new HttpObjectAggregator(finalMaxContentLength));
                            //ch.pipeline().addLast(new HttpDownloadHandler());
                            ch.pipeline().addLast(new Server());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_REUSEADDR, true) //重用地址
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // (6)
                   // .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false))  // heap buf 's better
                    .childOption(ChannelOption.TCP_NODELAY, true);

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync(); // (7)
            outUtil.outBlue("http服务器启动成功,端口:"+port);
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public ResourceBundle readProper(String configname){
        return ResourceBundle.getBundle(configname);
    }

}
