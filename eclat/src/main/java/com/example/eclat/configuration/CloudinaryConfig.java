package com.example.eclat.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dmjyr0m9b",
                "api_key", "929748567749484",
                "api_secret", "PTLrp9QOcKAZJmHTaDb448X8yXk",
                "secure", true
        ));
    }
}
