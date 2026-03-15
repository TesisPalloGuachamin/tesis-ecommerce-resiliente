package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.JwtProvider;
import com.tesis.ecommerce.coreapi.domain.port.out.PasswordEncoder;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.AuthResponse;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.UserDTO;
import com.tesis.ecommerce.coreapi.application.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    public RegisterUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                          JwtProvider jwtProvider, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.userMapper = userMapper;
    }

    public AuthResponse execute(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name(name)
            .enabled(true)
            .build();

        User savedUser = userRepository.save(user);
        String token = jwtProvider.generateToken(savedUser.getId(), savedUser.getEmail());
        UserDTO userDTO = userMapper.toDTO(savedUser);

        return AuthResponse.builder()
            .token(token)
            .user(userDTO)
            .build();
    }
}

