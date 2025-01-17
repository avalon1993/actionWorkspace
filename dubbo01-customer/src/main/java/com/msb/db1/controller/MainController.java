package com.msb.db1.controller;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msb.db1.service.DemoService;

@RestController
@RequestMapping("/test")
@Component
public class MainController {


	@Reference
	private DemoService demoService;


	// api/v888/server/method
	@RequestMapping("/main")
	public String main() {
		return demoService.sayHello("fuck");
	}
}
