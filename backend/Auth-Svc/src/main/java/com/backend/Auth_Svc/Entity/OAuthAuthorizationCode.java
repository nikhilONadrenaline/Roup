package com.backend.Auth_Svc.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "oauth_authorization_codes", indexes = @Index(name = "idx_oauth_codes_expiry", columnList = "expires_at"))
@Getter
@Setter
@NoArgsConstructor
public class OAuthAuthorizationCode {
    @Id
    @Column(name = "code_hash", nullable = false, length = 64)
    private String codeHash;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean consumed;
}
