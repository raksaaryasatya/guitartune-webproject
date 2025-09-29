package com.guitartune.project_raksa.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.guitartune.project_raksa.models.User;

@Service
public class GetAuthoritiesImpl implements GetAuthorities {
    @Override
    public User getAuthenticatedUser() throws Exception {
        // Ambil objek Authentication dari SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Validasi: Pastikan Authentication tidak null
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("Pengguna tidak terautentikasi");
        }

        // Validasi: Pastikan Principal adalah instance dari CustomUserDetails
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new Exception("Detail pengguna tidak valid");
        }

        // Ambil CustomUserDetails dan kembalikan objek User
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Validasi: Pastikan User tidak null
        if (userDetails.getUser() == null) {
            throw new Exception("Detail pengguna tidak ditemukan");
        }

        return userDetails.getUser();
    }
}
