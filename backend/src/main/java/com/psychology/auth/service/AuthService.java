package com.psychology.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.psychology.auth.exception.AuthenticatedUserNotFoundException;
import com.psychology.auth.entity.SysUser;
import com.psychology.auth.exception.InvalidCredentialsException;
import com.psychology.auth.exception.UsernameAlreadyExistsException;
import com.psychology.auth.mapper.SysUserMapper;
import com.psychology.auth.model.LoginRequest;
import com.psychology.auth.model.LoginResponse;
import com.psychology.auth.model.RegisterRequest;
import com.psychology.auth.model.UserProfile;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, request.username()));

        if (user == null || !Integer.valueOf(1).equals(user.getStatus())
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return issueLoginResponse(user);
    }

    public LoginResponse register(RegisterRequest request) {
        String username = request.username().trim();
        SysUser existing = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, username));
        if (existing != null) {
            throw new UsernameAlreadyExistsException();
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName() == null || request.displayName().isBlank()
                ? username
                : request.displayName().trim());
        user.setRole("USER");
        user.setStatus(1);
        try {
            sysUserMapper.insert(user);
        } catch (DuplicateKeyException exception) {
            throw new UsernameAlreadyExistsException();
        }
        return issueLoginResponse(user);
    }

    public SysUser requireActiveUser(String username) {
        SysUser user = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, username));
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())) {
            throw new AuthenticatedUserNotFoundException();
        }
        return user;
    }

    private LoginResponse issueLoginResponse(SysUser user) {
        String token = jwtService.issueToken(user);
        UserProfile profile = new UserProfile(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
        return new LoginResponse(token, "Bearer", jwtService.expirationSeconds(), profile);
    }
}
