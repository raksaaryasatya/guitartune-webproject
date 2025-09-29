package com.guitartune.project_raksa.dto.store;

import com.guitartune.project_raksa.dto.product.ProductResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductResponse {
    private String id;
    private Long total;
    private ProductResponse product;
}