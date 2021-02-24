package com.msb.db1.service;

import org.apache.dubbo.config.annotation.Service;

//@Service(version = "1.0.0" ,timeout = 10000, interfaceClass = DemoService.class)
public interface DemoService {

    String sayHello(String name);

}
