package com.guitartune.project_raksa.models;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @UuidGenerator
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id", length = 36, nullable = false)
    private String id;
    private String roleName;
}
