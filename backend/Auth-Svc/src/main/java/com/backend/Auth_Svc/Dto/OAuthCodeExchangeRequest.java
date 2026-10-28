package com.backend.Auth_Svc.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OAuthCodeExchangeRequest {
    @NotBlank
    @Size(max = 128)
    private String code;
}
