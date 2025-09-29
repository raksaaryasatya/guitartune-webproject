package com.guitartune.project_raksa.services.transaction;

import com.guitartune.project_raksa.dto.product.ProductResponse;
import com.guitartune.project_raksa.dto.transaction.TransactionRequest;
import com.guitartune.project_raksa.dto.transaction.TransactionResponse;
import com.guitartune.project_raksa.models.Product;
import com.guitartune.project_raksa.models.Store;
import com.guitartune.project_raksa.models.StoreProduct;
import com.guitartune.project_raksa.models.Transaction;
import com.guitartune.project_raksa.models.User;
import com.guitartune.project_raksa.repositorys.ProductRepository;
import com.guitartune.project_raksa.repositorys.StoreProductRepository;
import com.guitartune.project_raksa.repositorys.StoreRepository;
import com.guitartune.project_raksa.repositorys.TransactionRepository;
import com.guitartune.project_raksa.repositorys.UserRepository;
import com.guitartune.project_raksa.services.convert.ConvertImage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreProductRepository storeProductRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ConvertImage convertImage;

    @Override
    public TransactionResponse buy(String id, TransactionRequest transactionRequest) throws Exception {
        // Ambil user yang login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByUsername(auth.getName());
        if (user == null)
            throw new Exception("User not found");

        // Ambil produk
        Product product = productRepository.findProductById(id);
        if (product == null)
            throw new Exception("Product tidak ditemukan");

        // Validasi stok produk
        if (product.getQuantity() <= 0)
            throw new Exception("Produk habis");
        if (transactionRequest.getPurchaseQuantity() > product.getQuantity())
            throw new Exception("Quantity melebihi stok");

        // Validasi total payment
        Long expectedPayment = product.getPrice() * transactionRequest.getPurchaseQuantity();
        System.out.println("bayar : " + transactionRequest.getTotalPayment());
        if (transactionRequest.getTotalPayment() != expectedPayment){
            if(transactionRequest.getTotalPayment() < expectedPayment){
                throw new Exception("Total payment kurang dari harga produk Harap Bayar : " + expectedPayment);
            }else if(transactionRequest.getTotalPayment() > expectedPayment){
                throw new Exception("Total payment melebihi harga produk \n Harap Bayar : " + expectedPayment);
            }
        }

        // Validasi saldo user
        if (user.getSaldo() < transactionRequest.getTotalPayment())
            throw new Exception("Saldo tidak cukup");

        // Proses transaksi
        Transaction transaction = new Transaction();
        transaction.setPurchaseQuantity(transactionRequest.getPurchaseQuantity());
        transaction.setTotalPayment(transactionRequest.getTotalPayment());
        transaction.setProductId(product.getId());
        transaction.setDateTransaction(LocalDateTime.now());
        transaction.setUser(user);
        transaction.setProductName(product.getProductName());
        transactionRepository.save(transaction);

        // Update stok produk
        product.setQuantity(product.getQuantity() - transactionRequest.getPurchaseQuantity());
        productRepository.save(product);

        // Update saldo user
        user.setSaldo(user.getSaldo() - transaction.getTotalPayment());
        userRepository.save(user);

        // Update pemasukan toko
        StoreProduct storeProduct = storeProductRepository.findStoreProductByProduct(product);
        Store store = storeRepository.findStoreByStoreProducts(storeProduct);
        store.setIncome(store.getIncome() + transaction.getTotalPayment());
        storeRepository.save(store);

        // Buat response
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setUser(transaction.getUser());
        response.setProduct(toProduct(product));
        response.setDateTransaction(transaction.getDateTransaction());

        return response;
    }

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

    @Override
    public void deleteTransaction(String id) throws Exception {
        // Mencari produk berdasarkan ID
        Transaction delete = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        transactionRepository.delete(delete);
    }
}