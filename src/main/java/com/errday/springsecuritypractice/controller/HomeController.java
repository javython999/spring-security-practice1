package com.errday.springsecuritypractice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value={"/", "/dashboard"})
    public String dashboard() {
        return "/dashboard";
    }

    @GetMapping(value="/user")
    public String user() {
        return "/user";
    }

    @GetMapping(value="/manager")
    public String manager() {
        return "/manager";
    }

    @GetMapping(value="/admin")
    public String admin() {
        return "/admin";
    }

    @GetMapping(value="/dba")
    public String dba() {
        return "/dba";
    }

    @GetMapping("/api")
    public String restDashboard() {
        return "rest/dashboard";
    }
}
