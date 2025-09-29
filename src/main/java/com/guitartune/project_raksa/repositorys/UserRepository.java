package com.guitartune.project_raksa.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guitartune.project_raksa.models.User;

public interface UserRepository extends JpaRepository<User, String>{
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);
    List<User> findByUsernameStartingWith(String username);
    List<User> findAllByOrderByUsernameAsc();
    List<User> findAllByOrderByUsernameDesc();
}
