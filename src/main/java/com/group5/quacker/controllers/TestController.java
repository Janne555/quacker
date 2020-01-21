package com.group5.quacker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/teststuff")
    public String showForm() {
        return "nothing";
    }
}
