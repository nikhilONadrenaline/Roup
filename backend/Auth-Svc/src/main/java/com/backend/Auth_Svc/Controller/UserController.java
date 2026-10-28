package com.backend.Auth_Svc.Controller;


import com.backend.Auth_Svc.Dto.SigninDto;
import com.backend.Auth_Svc.Dto.SignupDto;
import com.backend.Auth_Svc.Dto.UserResponseDto;
import com.backend.Auth_Svc.Dto.OAuthCodeExchangeRequest;
import com.backend.Auth_Svc.Service.OAuthCodeService;
import com.backend.Auth_Svc.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final OAuthCodeService oauthCodeService;

    @PostMapping("/signup")
    public void userSignup(@RequestBody SignupDto signupDto)
    {
        userService.addUser(signupDto);
    }

    @PostMapping("/signin")
    public UserResponseDto userSignin(@RequestBody SigninDto signinDto)
    {
        return userService.loginUser(signinDto);
    }

    @PostMapping("/oauth/exchange")
    public UserResponseDto oauthExchange(@Valid @RequestBody OAuthCodeExchangeRequest request) {
        return oauthCodeService.exchange(request.getCode());
    }

}
