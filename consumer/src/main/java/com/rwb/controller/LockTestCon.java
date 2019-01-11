package com.rwb.controller;

import com.rwb.service.ZKLockTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LockTestCon {

    @Autowired
    private ZKLockTestService lockService;

    @GetMapping("/append")
    public String append(){
        lockService.Append();
        return "SUCCESS";
    }
    @GetMapping("/getNum")
    public String getNum(){
        return lockService.getNum();
    }
}
