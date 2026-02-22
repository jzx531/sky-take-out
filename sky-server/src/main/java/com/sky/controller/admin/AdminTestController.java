package com.sky.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/test")
@Api(tags = "管理端测试接口")
public class AdminTestController {

    @GetMapping("/hello")
    @ApiOperation("测试接口")
    public String hello() {
        return "admin test";
    }
}