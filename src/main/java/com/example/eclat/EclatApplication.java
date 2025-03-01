package com.example.eclat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // bật lên để hỗ trợ cho quản lý đơn hàng
public class EclatApplication {

	public static void main(String[] args) {
		SpringApplication.run(EclatApplication.class, args);
	}

}
