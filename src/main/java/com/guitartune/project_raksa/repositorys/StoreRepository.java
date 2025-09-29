package com.guitartune.project_raksa.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guitartune.project_raksa.models.Store;
import com.guitartune.project_raksa.models.StoreProduct;
import com.guitartune.project_raksa.models.User;

public interface StoreRepository extends JpaRepository<Store,String>{
    Store findStoreByStoreName(String storeName);
    Store findStoreByUser(User user);
    Store findStoreById(String id);

    Store findStoreByStoreProducts(StoreProduct storeProduct);
}
