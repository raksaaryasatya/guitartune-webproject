package com.guitartune.project_raksa.services.user;

import com.guitartune.project_raksa.dto.RegisterRequest;
import com.guitartune.project_raksa.dto.UpdateUserRequest;

public interface UserService {
    void register(RegisterRequest registerRequest) throws Exception;
    void updateUser(UpdateUserRequest updateUserRequest) throws Exception;
    void tambahSaldo(Long saldo) throws Exception;
    void deleteUser(String id) throws Exception;

}
