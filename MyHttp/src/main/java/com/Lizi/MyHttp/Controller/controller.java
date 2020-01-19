package com.Lizi.MyHttp.Controller;

import com.Lizi.MyHttp.Annot.*;
import com.Lizi.MyHttp.Util.outUtil;


@RestController(route = "/test")
public class controller {
//    @Before(logMsg = "myget()方法开始执行")
//    @After(logMsg = "myget()执行完毕")
    @MyAutowired
    testClass tc;
    @RestApi(url = "/myget",method = "GET")
    public String myget(String req){
        return "Get方法"+req;
    }
    @RestApi(url="/mypost",method = "POST")
    public String mypost(String req){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        outUtil.outBlue("connect Complete");
        return "Post方法"+req;
    }
}
