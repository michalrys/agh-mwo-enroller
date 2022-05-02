package com.company.enroller.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StartingPageController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
}
