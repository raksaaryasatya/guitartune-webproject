package com.guitartune.project_raksa.dto.transaction;

import java.time.LocalDateTime;

import com.guitartune.project_raksa.dto.product.ProductResponse;
import com.guitartune.project_raksa.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String id;
    private LocalDateTime dateTransaction;
    private Integer purchaseQuantity;
    private ProductResponse product;
    private Long totalPayment;
    private User user;


}
