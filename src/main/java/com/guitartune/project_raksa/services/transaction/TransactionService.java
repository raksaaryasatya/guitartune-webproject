package com.guitartune.project_raksa.services.transaction;

import com.guitartune.project_raksa.dto.product.ProductResponse;
import com.guitartune.project_raksa.dto.transaction.TransactionRequest;
import com.guitartune.project_raksa.dto.transaction.TransactionResponse;
import com.guitartune.project_raksa.models.Product;

public interface TransactionService {
    TransactionResponse buy(String id, TransactionRequest transactionRequest) throws Exception;
    public ProductResponse toProduct(Product product);
    void deleteTransaction(String id) throws Exception;
}
