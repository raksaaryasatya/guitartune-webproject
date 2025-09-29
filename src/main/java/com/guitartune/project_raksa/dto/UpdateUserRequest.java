package com.guitartune.project_raksa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String address;
    private String city;
    private Long saldo;
}
