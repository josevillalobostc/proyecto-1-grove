package com.app.grove.user.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.grove.user.domain.UserService;
import com.app.grove.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getUserInfo(){
        UserResponse response = userService.getUserInfo();
        return ResponseEntity.ok(response);
    }

}
