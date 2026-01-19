package com.fatec.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "/teste")
public class DemoControll {

    public String hello(){
        return "hello";
    }

}
