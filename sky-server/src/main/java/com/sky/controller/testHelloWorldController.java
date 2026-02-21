package com.sky.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = "测试接口")
public class testHelloWorldController {
    @RequestMapping("/hello")
    @ApiOperation(value = "测试接口")
    public String hello() {
        return "Hello World!";
    }
}
