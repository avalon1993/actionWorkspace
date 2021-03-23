package com.system.nettywork;

public class MainThread {

    public static void main(String[] args) {
        //这里不做关于io和业务的事情

        //1.创建io thread

//        SelectorThreadGroup stg = new SelectorThreadGroup(1);
        SelectorThreadGroup stg = new SelectorThreadGroup(3);

        //2. 应该吧监听的server 注册到某一个selector上

        stg.bind(9999);


    }
}
