package com.springboot.integration.filepooling.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leader")
public class LeaderDemoController {

    @GetMapping
    public String getLeaderStatus(){
        return "Leader Controller Invoked from the service";
    }
}
