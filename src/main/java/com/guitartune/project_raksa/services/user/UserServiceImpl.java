package com.guitartune.project_raksa.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.guitartune.project_raksa.constant.RoleConstant;
import com.guitartune.project_raksa.dto.RegisterRequest;
import com.guitartune.project_raksa.dto.UpdateUserRequest;
import com.guitartune.project_raksa.models.User;
import com.guitartune.project_raksa.repositorys.RoleRepository;
import com.guitartune.project_raksa.repositorys.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest registerRequest) throws Exception {
        User username = userRepository.findUserByUsername(registerRequest.getUsername());
        User email = userRepository.findUserByEmail(registerRequest.getEmail());
        User phone = userRepository.findUserByPhoneNumber(registerRequest.getPhoneNumber());

        if (!registerRequest.getUsername().matches("^[A-Za-z][A-Za-z\\d]{5,12}$")) {
            throw new Exception("Nama pengguna harus terdiri dari 6 hingga 13 karakter");
        }
        if (!registerRequest.getPhoneNumber().matches("^08\\d{10,11}$")) {
            throw new Exception("Nomor telepon harus dalam format 08xxxxxxxxxx");
        }
        if (username != null) {
            throw new Exception("Nama pengguna sudah ada");
        }
        if (phone != null) {
            throw new Exception("Nomor telepon sudah ada");
        }
        if (email != null) {
            throw new Exception("Email sudah ada");
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setAddress(registerRequest.getAddress());
        newUser.setCity(registerRequest.getCity());
        newUser.setSaldo(0L);
        newUser.setRole(roleRepository.findRoleByRoleName(RoleConstant.ROLE_USER));
        userRepository.save(newUser);
    }

    @Override
    public void updateUser(UpdateUserRequest updateUserRequest) throws Exception {
        // Ambil user yang login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByUsername(auth.getName());

        if (user == null)
            throw new Exception("Pengguna tidak ditemukan");

        // Validasi username
        if (updateUserRequest.getUsername() != null && !updateUserRequest.getUsername().isEmpty()) {
            if (!updateUserRequest.getUsername().matches("^[A-Za-z][A-Za-z\\d]{5,12}$")) {
                throw new Exception("Nama pengguna harus terdiri dari 6 hingga 13 karakter");
            }
            User usernameCheck = userRepository.findUserByUsername(updateUserRequest.getUsername());
            if (usernameCheck != null && !usernameCheck.getId().equals(user.getId())) {
                throw new Exception("Nama pengguna sudah ada");
            }
            user.setUsername(updateUserRequest.getUsername());
        }

        // Validasi phone number
        if (updateUserRequest.getPhoneNumber() != null && !updateUserRequest.getPhoneNumber().isEmpty()) {
            if (!updateUserRequest.getPhoneNumber().matches("^08\\d{8,12}$")) {
                throw new Exception("Nomor telepon harus dalam format 08xxxxxxxxxx");
            }

            User phoneCheck = userRepository.findUserByPhoneNumber(updateUserRequest.getPhoneNumber());
            if (phoneCheck != null && !phoneCheck.getId().equals(user.getId())) {
                throw new Exception("Nomor telepon sudah ada");
            }
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }

        // Validasi email
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
            User emailCheck = userRepository.findUserByEmail(updateUserRequest.getEmail());
            if (emailCheck != null && !emailCheck.getId().equals(user.getId())) {
                throw new Exception("Email sudah ada");
            }
            user.setEmail(updateUserRequest.getEmail());
        }

        // Validasi City
        if (updateUserRequest.getAddress() != null && !updateUserRequest.getAddress().isEmpty()) {
            user.setCity(updateUserRequest.getCity());
        }

        // Validasi Address
        if (updateUserRequest.getAddress() != null && !updateUserRequest.getAddress().isEmpty()) {
            user.setAddress(updateUserRequest.getAddress());
        }

        // Update password jika diperlukan
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            if (passwordEncoder.matches(updateUserRequest.getPassword(), user.getPassword())) {
                throw new Exception("Kata sandi tidak boleh sama dengan kata sandi saat ini");
            }
            user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        // Simpan perubahan
        userRepository.save(user);
    }

    @Override
    public void tambahSaldo(Long saldo) throws Exception {
        // Ambil user yang login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByUsername(auth.getName());

        if (user == null) {
            throw new Exception("Pengguna tidak ditemukan");
        }

        // Menghitung total saldo baru
        Long totalSaldo = user.getSaldo() + saldo;

        // Update saldo user
        user.setSaldo(totalSaldo);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) throws Exception {
        User deleteUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        userRepository.delete(deleteUser);
    }

}
