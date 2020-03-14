package com.datta.app.rest.controllers;

import com.datta.app.rest.dto.LockRequest;
import com.datta.app.services.LockUnlockDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class LockUnlockController {

    @Autowired
    LockUnlockDocService lockUnlockDocService;

    @PutMapping
    public boolean lock(@RequestBody LockRequest lockRequest){
        System.out.println(lockRequest.toString());
        return lockUnlockDocService.lock(lockRequest.getUserId(), lockRequest.getDocumentId());
    }

    @GetMapping
    public String test(){
        return "All good";
    }
}
