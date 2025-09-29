package com.guitartune.project_raksa.services.product;

import java.io.IOException;
import java.sql.SQLException;
// import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// import com.guitartune.project_raksa.dto.product.PageProductsResponse;
import com.guitartune.project_raksa.dto.product.ProductRequest;
import com.guitartune.project_raksa.dto.product.ProductResponse;
// import com.guitartune.project_raksa.models.Category;
import com.guitartune.project_raksa.models.Product;
import com.guitartune.project_raksa.models.Store;
import com.guitartune.project_raksa.models.StoreProduct;
import com.guitartune.project_raksa.models.User;
import com.guitartune.project_raksa.repositorys.CategoryRepository;
import com.guitartune.project_raksa.repositorys.ProductRepository;
import com.guitartune.project_raksa.repositorys.StoreProductRepository;
import com.guitartune.project_raksa.repositorys.StoreRepository;
import com.guitartune.project_raksa.services.GetAuthorities;
import com.guitartune.project_raksa.services.convert.ConvertImage;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreProductRepository storeProductRepository;

    @Autowired
    private ConvertImage convertImage;


    @Autowired
    private GetAuthorities getAuthorities;


    @Override
    public void addProduct(ProductRequest productRequest, MultipartFile file) throws Exception {
        User user = getAuthorities.getAuthenticatedUser();
        Store store = storeRepository.findStoreByUser(user);

        Product product = productRepository.findProductByProductNameIgnoreCase(productRequest.getProductName());

        if (product == null) {
            Product newProduct = new Product();
            newProduct.setProductName(productRequest.getProductName());
            newProduct.setPrice(productRequest.getPrice());
            newProduct.setCategory(categoryRepository.findCategoryByCategoryName(productRequest.getCategory()));
            newProduct.setImageProduct(new SerialBlob(file.getBytes()));
            newProduct.setQuantity(productRequest.getQuantity());
            newProduct.setDescription(productRequest.getDescription());
            productRepository.save(newProduct);

            StoreProduct storeProduct = new StoreProduct();
            storeProduct.setProduct(newProduct);
            storeProduct.setStore(store);
            storeProduct.setTotal(newProduct.getPrice() * (long) productRequest.getQuantity());
            storeProductRepository.save(storeProduct);

            store.getStoreProducts().add(storeProduct);
            storeRepository.save(store);
        } else {
            if (productRequest.getQuantity() < 0) {
                throw new Exception("Invalid Quantity");
            }
            boolean same = false;
            List<StoreProduct> storeProducts = store.getStoreProducts();
            for (StoreProduct storeProduct : storeProducts) {
                if (storeProduct.getProduct().equals(product)) {
                    same = true;
                    storeProduct.getProduct()
                            .setQuantity(storeProduct.getProduct().getQuantity() + productRequest.getQuantity());
                    break;
                }
            }
            if (same) {
                store.setStoreProducts(storeProducts);
                storeProductRepository.saveAll(storeProducts);
                storeRepository.save(store);
            }
        }
    }

    
    @Override
    public void editProduct(String id, ProductRequest productRequest, MultipartFile file) throws Exception {
        
        Product editProduct = productRepository.findProductById(id);
        
        if (editProduct == null) {
            throw new Exception("Produk dengan ID tersebut tidak ditemukan.");
        }

        // Update stok produk
        if (productRequest.getQuantity() != null) {
            // Validasi inputan stok
            if (productRequest.getQuantity() < 0) {
                throw new Exception("Mohon Masukkan Stock yang Valid");
            }
            editProduct.setQuantity(productRequest.getQuantity());
        }
        
        
        
        // Update nama produk
        if (productRequest.getProductName() != null && !productRequest.getProductName().isEmpty()) {
            editProduct.setProductName(productRequest.getProductName());
        }

        // Update deskripsi produk
        if (productRequest.getDescription() != null && !productRequest.getDescription().isEmpty()) {
            editProduct.setDescription(productRequest.getDescription());
        }

        // Update harga produk
        if (productRequest.getPrice() != null && productRequest.getPrice() > 0) {
            editProduct.setPrice(productRequest.getPrice());
        }

        // Update kategori produk
        if (productRequest.getCategory() != null) {
            editProduct.setCategory(
                categoryRepository.findCategoryByCategoryName(productRequest.getCategory()));
            }

        

        // Update gambar produk (jika ada file baru yang diunggah)
        if (file != null && !file.isEmpty()) {
            if (!file.getContentType().startsWith("image")) {
                throw new Exception("Mohon Masukkan File Gambar");
            }
            editProduct.setImageProduct(new SerialBlob(file.getBytes()));
        }

        // Simpan perubahan ke database
        productRepository.save(editProduct);
        
    }
    
    @Override
    public void deleteProduct(String id) throws Exception {
        // Mencari produk berdasarkan ID
        Product deleteProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));
        

        // Mencari semua StoreProduct yang terkait dengan produk
        List<StoreProduct> storeProducts = storeProductRepository.findProductByProduct(deleteProduct);

        // Menghapus semua StoreProduct yang terkait
        if (!storeProducts.isEmpty()) {
            storeProductRepository.deleteAll(storeProducts);
        }

        // Menghapus produk
        productRepository.delete(deleteProduct);
    }
        
    @Override
    public ProductResponse toProduct(Product product) {
        try {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(product.getId());
            productResponse.setProductName(product.getProductName());
            productResponse.setPrice(product.getPrice());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setCategory(product.getCategory().getCategoryName());
            productResponse.setImageProduct(convertImage.convertImage(product.getImageProduct()));
            return productResponse;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
