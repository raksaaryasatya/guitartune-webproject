package com.guitartune.project_raksa.services.store;

import org.springframework.web.multipart.MultipartFile;

import com.guitartune.project_raksa.dto.store.RegisterStoreRequest;
import com.guitartune.project_raksa.dto.store.StoreProductResponse;
import com.guitartune.project_raksa.dto.store.StoreResponse;
import com.guitartune.project_raksa.models.StoreProduct;

public interface StoreService {
    void registerStore(RegisterStoreRequest requestStore, MultipartFile file) throws Exception;
    StoreResponse getStore() throws Exception;
    void withdrawIncome(Long jum) throws Exception;
    StoreProductResponse toStoreProductResponse(StoreProduct storeProduct);
}
