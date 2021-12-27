package com.api.wds.controller;

import com.api.wds.service.WdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author rohit
 * @Date 24/12/21
 **/
@RestController
@RequestMapping("wds")
public class WdsController {

    @Autowired
    private WdsService service;

    @GetMapping
    public ResponseEntity<?> send(){
        return new ResponseEntity<>(service.create(),HttpStatus.OK);
    }

}
