package com.company.enroller.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartingPageController {

    @RequestMapping(value = "/")
    @ResponseBody
    public String mainPage() {
        return "index";
    }
}
