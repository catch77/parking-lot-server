package com.oocl.ita.ivy.parkinglot.entity.controller;

import com.itmuch.lightsecurity.annotation.PreAuthorize;
import com.itmuch.lightsecurity.jwt.UserOperator;
import com.oocl.ita.ivy.parkinglot.entity.User;
import com.oocl.ita.ivy.parkinglot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("anon()")
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PreAuthorize("anon()")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user) {
        return ResponseEntity.ok(userService.login(user));
    }

    @GetMapping("/me")
    public com.itmuch.lightsecurity.jwt.User roleTest() {
        return userService.getSecurityUser();
    }
}
