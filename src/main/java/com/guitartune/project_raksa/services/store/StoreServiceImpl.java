package com.guitartune.project_raksa.services.store;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.guitartune.project_raksa.dto.store.RegisterStoreRequest;
import com.guitartune.project_raksa.dto.store.StoreProductResponse;
import com.guitartune.project_raksa.dto.store.StoreResponse;
import com.guitartune.project_raksa.models.Store;
import com.guitartune.project_raksa.models.StoreProduct;
import com.guitartune.project_raksa.models.User;
import com.guitartune.project_raksa.repositorys.StoreRepository;
import com.guitartune.project_raksa.repositorys.UserRepository;
import com.guitartune.project_raksa.services.GetAuthorities;
import com.guitartune.project_raksa.services.convert.ConvertImage;
import com.guitartune.project_raksa.services.product.ProductService;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ConvertImage convertImage;

    @Autowired
    private ProductService productService;

    @Autowired
    private GetAuthorities getAuthorities;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void registerStore(RegisterStoreRequest requestStore, MultipartFile file) throws Exception {
        // mencari store berdasarkan nama store
        Store store = storeRepository.findStoreByStoreName(requestStore.getStoreName());
        // validasi untuk store
        if (store != null) {
            throw new Exception("Store already exists");
        }
        // pengecekan apakan image yang di masukkan sudah sesuai dengan format
        // image(png,jpg,img,dll)
        if (!file.getContentType().startsWith("image")) {
            throw new Exception("Mohon Masukkan File Gambar");
        }
        User user = getAuthorities.getAuthenticatedUser();
        // membuat objek store
        store = new Store();
        // mengisi field store
        store.setIncome(0L);
        store.setExpence(0L);
        store.setRegisterDate(LocalDate.now());
        store.setStoreName(requestStore.getStoreName());
        store.setUser(user);
        store.setStoreImage(new SerialBlob(file.getBytes()));
        // menyimpan store ke database
        storeRepository.save(store);
    }

    @Override
    public StoreResponse getStore() throws Exception {
        User user = getAuthorities.getAuthenticatedUser();
        // mencari store berdasarkan user
        Store store = storeRepository.findStoreByUser(user);
        // validasi untuk store
        if (store == null) {
            // kalau kosong kembalikan ke home
            throw new Exception("/home");
        }

        StoreResponse response = new StoreResponse();
        response.setId(store.getId());
        response.setStoreName(store.getStoreName());
        response.setStoreImage(convertImage.convertImage(store.getStoreImage()));
        response.setIncome(store.getIncome());
        response.setExpence(store.getExpence());
        response.setRegisterDate(store.getRegisterDate());
        response.setUserStore(user);
        List<StoreProductResponse> listStoreProduct = new ArrayList<>();
        for (StoreProduct storeProduct : store.getStoreProducts()) {
            listStoreProduct.add(toStoreProductResponse(storeProduct));
        }
        response.setStoreProducts(listStoreProduct);
        
        return response;
    }

    @Override
    public StoreProductResponse toStoreProductResponse(StoreProduct storeProduct) {
        StoreProductResponse response = new StoreProductResponse();
        response.setId(storeProduct.getId());
        response.setTotal(storeProduct.getTotal());
        response.setProduct(productService.toProduct(storeProduct.getProduct()));
        return response;
    }

    @Override
    public void withdrawIncome(Long jum) throws Exception {
        User user = getAuthorities.getAuthenticatedUser();
        Store store = storeRepository.findStoreByUser(user);
        if (store.getIncome() >= jum) {
            store.setIncome(store.getIncome() - jum);
            store.setExpence(jum + store.getExpence());
            storeRepository.save(store);

            user.setSaldo(jum + user.getSaldo());
            userRepository.save(user);
        } else {
            throw new Exception("Dana tidak mencukupi");
        }
    }

}
