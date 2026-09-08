package com.peersphere.service;

import com.peersphere.dto.request.LoginRequest;
import com.peersphere.dto.request.RegisterRequest;
import com.peersphere.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}