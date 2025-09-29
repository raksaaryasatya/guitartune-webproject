package com.guitartune.project_raksa.models;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    @Column(name = "store_id", length = 36, nullable = false)
    private String id;

    private String storeName;
    private Long income;
    private Long expence;
    
    @Lob
    private Blob storeImage;
    private LocalDate registerDate;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;  

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreProduct> storeProducts = new ArrayList<>();

    @Transient // Tidak akan disimpan di database
    private String base64StoreImage;
}
