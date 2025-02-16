package com.example.eclat.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
                             @Value("${cloudinary.api-key}") String apiKey,
                             @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(

                "dmjyr0m9b", cloudName,
                "929748567749484", apiKey,
                "PTLrp9QOcKAZJmHTaDb448X8yXk", apiSecret
        ));
    }

    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );
            return uploadResult.get("url").toString(); // Lấy URL ảnh sau khi upload
        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại!", e);
        }
    }

    public void deleteFile(String imageUrl) {
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa ảnh trên Cloudinary: " + e.getMessage());
        }
    }

    // Hàm hỗ trợ để lấy publicId từ URL ảnh Cloudinary
    private String extractPublicIdFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf(".")); // Cắt chuỗi để lấy publicId
    }

}