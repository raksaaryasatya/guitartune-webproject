package com.guitartune.project_raksa.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String id;
    private String productName;
    private Long price;
    private Integer quantity;
    private String category;
    private String description;
}
