package com.app.grove.user.domain;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.grove.user.dto.UserResponse;
import com.app.grove.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        java.util.List<User> accounts = userRepository.findByUsername(username);
        if (accounts.isEmpty()) throw new UsernameNotFoundException("Usuario no encontrado con correo o nombre de usuario: " + username);
        User user = accounts.get(0);
        return user;
    }

    public UserResponse getUserInfo(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User usuario = (User) auth.getPrincipal();
        UserResponse response = modelMapper.map(usuario, UserResponse.class);
        return response;
    }
}