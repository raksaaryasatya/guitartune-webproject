package com.guitartune.project_raksa.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guitartune.project_raksa.models.Category;

public interface CategoryRepository extends JpaRepository<Category,String>{
    Category findCategoryByCategoryName(String categoryName);
}
