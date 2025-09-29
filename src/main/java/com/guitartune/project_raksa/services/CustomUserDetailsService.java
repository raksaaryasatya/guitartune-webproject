package com.guitartune.project_raksa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.guitartune.project_raksa.models.User;
import com.guitartune.project_raksa.repositorys.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository; // Mendeklarasikan UserRepository untuk mengakses data pengguna

    // Mengimplementasikan loadUserByUsername dari UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Mencari pengguna berdasarkan username
        User user = userRepository.findUserByUsername(username);
        
        // Jika pengguna tidak ditemukan, lempar exception UsernameNotFoundException
        if (user == null) {
            throw new UsernameNotFoundException("Username Atau Password Salah");
        }

        // Jika pengguna ditemukan, kembalikan CustomUserDetails yang berisi informasi pengguna
        return new CustomUserDetails(user);
    }
}
