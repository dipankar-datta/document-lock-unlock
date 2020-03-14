package com.datta.app.rest.controllers;

import com.datta.app.rest.dto.LockRequest;
import com.datta.app.services.LockUnlockDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class LockUnlockController {

    @Autowired
    LockUnlockDocService lockUnlockDocService;

    @PutMapping(path = "/lock")
    public boolean lock(@RequestBody LockRequest lockRequest){
        return lockUnlockDocService.lock(lockRequest.getUserId(), lockRequest.getDocumentId());
    }

    @PutMapping(path = "/unlock")
    public boolean unlock(@RequestBody LockRequest lockRequest){
        return lockUnlockDocService.unlock(lockRequest.getUserId(), lockRequest.getDocumentId());
    }

    @GetMapping
    public String test(){
        return "All good";
    }
}
