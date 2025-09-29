package com.guitartune.project_raksa.services;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.guitartune.project_raksa.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private User user; // Menyimpan objek User yang berisi informasi pengguna yang terautentikasi
    
    // Method untuk mendapatkan otoritas pengguna (role)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mengembalikan role pengguna sebagai SimpleGrantedAuthority
        // Role diambil dari user dan diubah menjadi otoritas
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRoleName()));
    }
    
    // Method untuk mendapatkan password pengguna
    @Override
    public String getPassword() {
        return user.getPassword(); // Mengembalikan password pengguna dari objek User
    }

    // Method untuk mendapatkan username pengguna
    @Override
    public String getUsername() {
        return user.getUsername(); // Mengembalikan username pengguna dari objek User
    }

    // Method tambahan yang diperlukan oleh UserDetails

    // Method untuk mengecek apakah akun pengguna sudah expired atau tidak
    @Override
    public boolean isAccountNonExpired() {
        return true; // Mengembalikan true, berarti akun tidak expired
    }

    // Method untuk mengecek apakah akun pengguna sudah terkunci atau tidak
    @Override
    public boolean isAccountNonLocked() {
        return true; // Mengembalikan true, berarti akun tidak terkunci
    }

    // Method untuk mengecek apakah kredensial pengguna sudah expired atau tidak
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Mengembalikan true, berarti kredensial tidak expired
    }

    // Method untuk mengecek apakah akun pengguna aktif atau tidak
    @Override
    public boolean isEnabled() {
        return true; // Mengembalikan true, berarti akun aktif
    }
}
