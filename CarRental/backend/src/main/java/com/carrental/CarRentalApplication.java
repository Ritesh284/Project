package com.carrental;

import com.carrental.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {
        EnvLoader.loadEnv();
        SpringApplication.run(CarRentalApplication.class, args);
    }
}
