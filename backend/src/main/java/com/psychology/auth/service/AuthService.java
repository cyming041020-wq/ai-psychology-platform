package com.psychology.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.psychology.auth.entity.SysUser;
import com.psychology.auth.exception.InvalidCredentialsException;
import com.psychology.auth.mapper.SysUserMapper;
import com.psychology.auth.model.LoginRequest;
import com.psychology.auth.model.LoginResponse;
import com.psychology.auth.model.UserProfile;

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

        String token = jwtService.issueToken(user);
        UserProfile profile = new UserProfile(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
        return new LoginResponse(token, "Bearer", jwtService.expirationSeconds(), profile);
    }
}
