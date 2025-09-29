package com.guitartune.project_raksa.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guitartune.project_raksa.models.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    Product findProductByProductNameIgnoreCase(String productNama);

    Product findProductById(String id);

    List<Product> findByProductNameStartingWith(String prefix);

    List<Product> findByCategory_CategoryName(String categoryName);

    List<Product> findAllByOrderByPriceAsc();

    List<Product> findAllByOrderByPriceDesc();

}
