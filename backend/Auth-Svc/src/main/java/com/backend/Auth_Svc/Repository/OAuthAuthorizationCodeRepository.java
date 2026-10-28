package com.backend.Auth_Svc.Repository;

import com.backend.Auth_Svc.Entity.OAuthAuthorizationCode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface OAuthAuthorizationCodeRepository extends JpaRepository<OAuthAuthorizationCode, String> {
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update OAuthAuthorizationCode c set c.consumed = true where c.codeHash = :hash and c.consumed = false and c.expiresAt > :now")
    int consumeIfValid(@Param("hash") String hash, @Param("now") Instant now);
}
