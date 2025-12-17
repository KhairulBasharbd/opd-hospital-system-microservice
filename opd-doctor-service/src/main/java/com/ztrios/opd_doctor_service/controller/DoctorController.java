package com.ztrios.opd_doctor_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/test")
public class DoctorController {

    @ResponseBody
    @GetMapping
    public String hello(){

        return "Hello World!! ";
    }
}
