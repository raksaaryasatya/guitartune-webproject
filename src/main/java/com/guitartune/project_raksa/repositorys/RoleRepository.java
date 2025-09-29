package com.guitartune.project_raksa.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guitartune.project_raksa.models.Role;

public interface RoleRepository extends JpaRepository<Role, String>{
    Role findRoleByRoleName(String roleName);
}
