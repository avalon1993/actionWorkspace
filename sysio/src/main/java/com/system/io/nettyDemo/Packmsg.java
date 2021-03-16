package com.system.io.nettyDemo;

public class Packmsg {

    MyHeader header;
    MyContent content;

    public Packmsg(MyHeader header, MyContent content) {
        this.header = header;
        this.content = content;
    }
}
