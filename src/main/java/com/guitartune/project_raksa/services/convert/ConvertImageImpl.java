package com.guitartune.project_raksa.services.convert;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class ConvertImageImpl implements ConvertImage {

    // Implementasi metode convertImage untuk mengonversi gambar dari tipe Blob ke format Base64
    @Override
    public String convertImage(Blob image) throws IOException, SQLException {
        // Mengambil byte array dari objek Blob (gambar) 
        byte[] bytes = image.getBytes(1, (int) image.length());
        
        // Mengonversi byte array menjadi string dalam format Base64
        return Base64.getEncoder().encodeToString(bytes);
    }   
}
