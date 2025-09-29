package com.guitartune.project_raksa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guitartune.project_raksa.dto.product.ProductRequest;
import com.guitartune.project_raksa.services.product.ProductService;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    // @Autowired
    // private UserRepository userRepository;

    // @Autowired
    // private StoreRepository storeRepository;

    // @Autowired
    // private StoreProductRepository storeProductRepository;

    @PostMapping("/save-product")
    public String saveProduct(ProductRequest productRequest, @RequestPart("file") MultipartFile file,
            RedirectAttributes redirectAttributes, Model model) {
        try {
            productService.addProduct(productRequest, file);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/stores/home-store";
    }

    @PostMapping("/save-edit-product/{id}")
    public String saveProduct(@PathVariable(value = "id") String id, ProductRequest productRequest,
            RedirectAttributes redirectAttributes, @RequestPart("file") MultipartFile file) {
        try {
            productService.editProduct(id, productRequest, file);
            redirectAttributes.addFlashAttribute("message", "User updated successfully");
            return "redirect:/stores/home-store";
        } catch (Exception e) {
            System.out.println("gagal");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/product/edit-product/" + id;
        }
    }
}
