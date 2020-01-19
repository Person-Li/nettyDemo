package com.Lizi.MyHttp.NettyServer;

import com.Lizi.MyHttp.Util.outUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
public class Request {
    private FullHttpRequest fullHttpRequest;
    private String requestType;
    public Request(FullHttpRequest req){
        fullHttpRequest=req;
        requestType=fullHttpRequest.method().name();
    }
    public String getURI(){
        String uri=fullHttpRequest.uri();
        int index=uri.indexOf('?');
        if(index>-1){
            return uri.substring(0,index);
        }
        return uri;
    }
    public String getMethodName(){
        return fullHttpRequest.method().name();
    }
    public String getParam() throws UnsupportedEncodingException {
        if(requestType.equals("GET")){
//            QueryStringDecoder queryStringDecoder=new QueryStringDecoder(fullHttpRequest.uri());
//            return queryStringDecoder.parameters().toString();
            String uri=fullHttpRequest.uri();
            int index=uri.indexOf('?')+1;
            if(index>0){
                return URLDecoder.decode(uri.substring(index),"UTF-8");
            }
            return null;
        }else
        if(requestType.equals("POST")){
            ByteBuf buf=fullHttpRequest.content();
            return buf.toString(Charset.forName("UTF-8"));
        }else {
            outUtil.outYellow("不合法的请求方式:"+requestType);
            return null;
        }
    }
}
