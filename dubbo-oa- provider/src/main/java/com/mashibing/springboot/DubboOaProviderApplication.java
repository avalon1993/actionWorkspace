package com.mashibing.springboot;

import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.mashibing.springboot.mapper")
@EnableDubboConfig
public class DubboOaProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboOaProviderApplication.class, args);
	}

}
