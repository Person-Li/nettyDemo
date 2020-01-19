package com.Lizi.MyHttp.Util;

public class outUtil {
    public outUtil(){}
    public static<T> void outBlue(T msg){
        System.out.println("\033[96;2m" + msg + "\033[0m");
    }
    public static<T> void outYellow(T msg){
        System.out.println("\033[93;2m" + msg + "\033[0m");
    }
    public static<T> void outErr(T msg){
        System.err.println(msg);
    }
}
