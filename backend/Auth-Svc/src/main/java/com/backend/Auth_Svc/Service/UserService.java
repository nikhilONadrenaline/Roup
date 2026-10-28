package com.backend.Auth_Svc.Service;

import com.backend.Auth_Svc.Dto.SigninDto;
import com.backend.Auth_Svc.Dto.SignupDto;
import com.backend.Auth_Svc.Dto.UserResponseDto;
import com.backend.Auth_Svc.Entity.User;
import com.backend.Auth_Svc.Enum.ExpertStatus;
import com.backend.Auth_Svc.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    public void addUser(SignupDto signupDto) {
        if (userRepository.existsByEmailId(signupDto.getEmailId())) {
            log.error("Account already exists for email id : {}", signupDto.getEmailId());
            throw new BadCredentialsException("Email Id already registered");
        }

        User user = new User();
        user.setName(signupDto.getName());
        user.setEmailId(signupDto.getEmailId());
        user.setPassword(bCryptPasswordEncoder.encode(signupDto.getPassword()));

        user.setExpertStatus(ExpertStatus.NOT_REGISTERED);
        user.setTotalBalance(BigDecimal.valueOf(0.0));
        user.setAvailableBalance(BigDecimal.valueOf(0.0));
        user.setReservedBalance(BigDecimal.valueOf(0.0));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public UserResponseDto loginUser(SigninDto signinDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinDto.getEmailId(), signinDto.getPassword())
        );

        User user = userRepository.findByEmailId(signinDto.getEmailId());

        return responseFor(user, jwtService.generateToken(signinDto.getEmailId(), user.getId()));
    }

    public User findOrCreateOAuthUser(String email, String name, String avatar) {
        User existing = userRepository.findByEmailId(email);
        if (existing != null) return existing;
        User user = new User();
        user.setEmailId(email);
        user.setName(name == null || name.isBlank() ? email.substring(0, email.indexOf('@')) : name);
        user.setAvatar(avatar);
        user.setExpertStatus(ExpertStatus.NOT_REGISTERED);
        user.setTotalBalance(BigDecimal.ZERO);
        user.setAvailableBalance(BigDecimal.ZERO);
        user.setReservedBalance(BigDecimal.ZERO);
        user.setCreatedAt(LocalDateTime.now());
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException concurrentCreate) {
            User winner = userRepository.findByEmailId(email);
            if (winner != null) return winner;
            throw concurrentCreate;
        }
    }

    public UserResponseDto responseFor(User user, String token) {
        UserResponseDto response = new UserResponseDto();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmailId(user.getEmailId());
        response.setAvatar(user.getAvatar());
        response.setExpertStatus(user.getExpertStatus());
        response.setTotalBalance(user.getTotalBalance());
        response.setAvailableBalance(user.getAvailableBalance());
        response.setReservedBalance(user.getReservedBalance());
        response.setCreatedAt(user.getCreatedAt());
        response.setJwt(token);

        return response;
    }
}
