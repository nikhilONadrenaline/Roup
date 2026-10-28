package com.backend.Auth_Svc.Service;

import com.backend.Auth_Svc.Dto.UserResponseDto;
import com.backend.Auth_Svc.Entity.OAuthAuthorizationCode;
import com.backend.Auth_Svc.Entity.User;
import com.backend.Auth_Svc.Repository.OAuthAuthorizationCodeRepository;
import com.backend.Auth_Svc.Repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class OAuthCodeService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long CODE_TTL_SECONDS = 180;
    private final OAuthAuthorizationCodeRepository codes;
    private final UserRepository users;
    private final UserService userService;
    private final JWTService jwtService;

    public OAuthCodeService(OAuthAuthorizationCodeRepository codes, UserRepository users, UserService userService, JWTService jwtService) {
        this.codes = codes;
        this.users = users;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public String issueFor(User user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        OAuthAuthorizationCode record = new OAuthAuthorizationCode();
        record.setCodeHash(hash(raw));
        record.setUserId(user.getId());
        record.setExpiresAt(Instant.now().plusSeconds(CODE_TTL_SECONDS));
        record.setConsumed(false);
        codes.save(record);
        return raw;
    }

    @Transactional
    public UserResponseDto exchange(String rawCode) {
        if (rawCode == null || rawCode.length() > 128 || codes.consumeIfValid(hash(rawCode), Instant.now()) != 1) {
            throw new BadCredentialsException("OAuth sign-in code is invalid or expired");
        }
        OAuthAuthorizationCode record = codes.findById(hash(rawCode))
                .orElseThrow(() -> new BadCredentialsException("OAuth sign-in code is invalid or expired"));
        User user = users.findById(record.getUserId())
                .orElseThrow(() -> new BadCredentialsException("Roup account is unavailable"));
        return userService.responseFor(user, jwtService.generateToken(user.getEmailId(), user.getId()));
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
