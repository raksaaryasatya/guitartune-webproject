package com.guitartune.project_raksa.dto.store;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.guitartune.project_raksa.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {
    private String id;
    private String storeName;
    private String storeImage;
    private Long income;
    private Long expence;
    private LocalDate registerDate;
    private User userStore;
    private List<StoreProductResponse> storeProducts = new ArrayList<>();
}
