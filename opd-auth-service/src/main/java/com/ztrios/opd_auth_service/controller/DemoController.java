package com.ztrios.opd_auth_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class DemoController {

    @ResponseBody
    @GetMapping
    public String hello(){

        return "Hello World!! Juwel ";
    }
}
