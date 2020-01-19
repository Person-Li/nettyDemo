package com.Lizi.MyHttp;

import com.Lizi.MyHttp.NettyServer.StartServer;

public class Start {
    public static void main(String[] args) {
        try {
            new StartServer("ReadTest");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
